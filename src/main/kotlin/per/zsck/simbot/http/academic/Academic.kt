package per.zsck.simbot.http.academic

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import org.apache.commons.codec.digest.DigestUtils
import org.apache.http.Header
import org.apache.http.HttpHeaders
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHeader
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import per.zsck.simbot.common.logError
import per.zsck.simbot.common.logInfo
import per.zsck.simbot.common.utils.HttpBase
import per.zsck.simbot.http.academic.entity.ClassMap
import per.zsck.simbot.http.academic.entity.Schedule
import per.zsck.simbot.http.academic.service.ClassMapService
import per.zsck.simbot.http.academic.service.ScheduleService
import javax.annotation.PostConstruct


/**
 * @author zsck
 * @date   2022/11/8 - 14:53
 */
@Component
class Academic(
    val scheduleService: ScheduleService,
    val classMapService: ClassMapService
): HttpBase() {

    @Value("\${zsck.academic.user-name}")
    lateinit var userName: String

    @Value("\${zsck.academic.password}")
    lateinit var password: String


    fun refresh(): Boolean{
        logInfo("开始刷新表 class_map 及表 schedule 中内容...")
        logInfo("清除表 class_map {} 条记录, 清除表 schedule {} 条记录", classMapService.removeAll(), scheduleService.removeAll())

        try {
            //访问中间网址，获取会话cookie和加密密钥
            val encode = DigestUtils.sha1Hex("${doGetStr(GET_SALT)}-${password}") //密码加密

            //登录验证


            val entity = StringEntity(
                objectMapper.writeValueAsString(
                    mutableMapOf(Pair("username", userName), Pair("password", encode), Pair("captcha", ""))
                ), "UTF-8"
            )
            entity.setContentType("application/json")
            val loginRes = doPostJson(LOGIN_URL, entity) //登录结果

            //访问我的课程表，获取课程表中lessons的id
            val lessonIds = doGetJson( LESSON_FOR_ID )["lessonIds"]
            //TODO 待确认lessonIds.toString()结果

            //再次请求，根据lessonIds请求得到具体lesson信息
            val entityForRes = StringEntity(
                    objectMapper.writeValueAsString(
                        mutableMapOf(Pair("lessonIds", lessonIds), Pair("studentId", 152113), Pair("weekIndex", ""))
                    ), "UTF-8"
            )

            entityForRes.setContentType("application/json")
            val lessonsRes = doPostJson(LESSON_URL, entityForRes)

            //对返回的lesson信息进行解析
            try {
                val scheduleList = ArrayList<Schedule>().apply {
                    lessonsRes["result"]["scheduleList"].forEach { res: JsonNode ->
                        try {
                            val schedule: Schedule = objectMapper.readValue(res.toString(), Schedule::class.java)
                            schedule.room = res["room"]["nameZh"].asText()
                            this.add(schedule)
                        } catch (e: JsonProcessingException) {
                            logError("解析lesson信息错误: {}", e.message?: "")
                            e.printStackTrace()
                        }
                    }
                }

                val classMapList = ArrayList<ClassMap>().apply {
                    lessonsRes["result"]["lessonList"].forEach{ res ->
                        this.add(
                            ClassMap(res["id"].asInt(), res["courseName"].asText())
                        )
                    }
                }

                logInfo("表schedule新增数据:" + scheduleService.saveBatch(scheduleList).toString() + "条")
                logInfo("表class_map新增数据:" + classMapService.saveBatch(classMapList).toString() + "条")
                return true
            } catch (e: Exception) {
                logError("数据解析错误: {}", e.message?: "")
                e.printStackTrace()
            }
        } catch (e: Exception) {
            logError("访问网址错误: {}", e.message?: "")
            e.printStackTrace()
        }
        return false
    }



    override fun getHeader(): Array<Header>? {
        return arrayOf(BasicHeader(HttpHeaders.USER_AGENT, USER_AGENT), BasicHeader(HttpHeaders.REFERER, REFERER))
    }

    companion object{
        const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 SLBrowser/8.0.0.3161 SLBChan/103"
        const val REFERER =
            "http://jxglstu.hfut.edu.cn/eams5-student/login?refer=http://jxglstu.hfut.edu.cn/eams5-student/for-std/course-table/info/152113"


        const val GET_SALT = "http://jxglstu.hfut.edu.cn/eams5-student/login-salt"
        const val LOGIN_URL = "http://jxglstu.hfut.edu.cn/eams5-student/login"
        const val LESSON_URL = "http://jxglstu.hfut.edu.cn/eams5-student/ws/schedule-table/datum"
        const val LESSON_FOR_ID = "http://jxglstu.hfut.edu.cn/eams5-student/for-std/course-table/get-data?bizTypeId=23&semesterId=194&dataId=152113"
    }
}