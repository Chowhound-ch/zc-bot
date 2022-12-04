package per.zsck.simbot.http.academic.util

import cn.hutool.core.date.DateUtil
import love.forte.simbot.message.Messages
import love.forte.simbot.message.MessagesBuilder
import org.springframework.stereotype.Component
import per.zsck.simbot.http.academic.entity.ClassMap
import per.zsck.simbot.http.academic.entity.Schedule
import per.zsck.simbot.http.academic.service.ClassMapService
import java.sql.Date
import java.time.temporal.ChronoField
import java.util.stream.Collectors
import javax.annotation.PostConstruct

/**
 * @author zsck
 * @date   2022/11/10 - 9:11
 */
@Component
class AcademicUtil(
    val classMapService: ClassMapService
) {

    lateinit var mutableClassMap: MutableMap<Int, String>

    @PostConstruct
    fun init(){
        mutableClassMap = classMapService.list().stream().collect(Collectors.toMap({ item -> item.id}, ClassMap::className))
    }

    fun getLessonInfoMsg(classMap: ClassMap, classDetail: Map<String, Any> ): String{
        val builder = StringBuilder()
        builder.append("课程:${classMap.className}")

        if (classDetail.isNotEmpty()) {
            builder.append("\n开课周次: ${classDetail["start"]} - ${classDetail["end"]}")
                .append("\n授课教师: ${classDetail["person_name"]}")
            return builder.toString()
        }
        return builder.append("暂无信息").toString()
    }

    fun getCourseDetailMsg(scheduleList: List<Schedule>): List<Messages>{
        var builder = MessagesBuilder()
        scheduleList.stream().collect(Collectors.groupingBy(Schedule::date))
        var today: Date? = null
        val messagesList = ArrayList<Messages>()

        for (schedule in scheduleList) {
            val th = schedule.date

            if (today == null || th != today){
                if (today != null){
                    messagesList.add(builder.build())
                    builder = MessagesBuilder()
                }

                val i: Int = schedule.date.toLocalDate().get(ChronoField.DAY_OF_WEEK)
                builder.append(DateUtil.format(th, "MM-dd") + " 周" + WeekUtil.WEEKDAY[i - 1] + ":")
                today = th
            }
            if (th == today) {
                val start = String.format("%02d", schedule.startTime / 100) + ":" + String.format("%02d", schedule.startTime % 100)

                val end = String.format("%02d", schedule.endTime / 100) + ":" + String.format("%02d", schedule.endTime % 100)
                builder.append("\n * $start - $end ${schedule.room} ${mutableClassMap[schedule.lessonId]} ${schedule.personName}")
            }
        }

        return messagesList.apply { this.add(builder.build()) }
    }
}

object WeekUtil {
    val WEEKDAY = arrayOf("一", "二", "三", "四", "五", "六", "日")
}
