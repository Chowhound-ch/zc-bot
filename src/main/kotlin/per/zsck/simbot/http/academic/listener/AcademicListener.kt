package per.zsck.simbot.http.academic.listener

import cn.hutool.core.date.DateUnit
import cn.hutool.core.date.DateUtil
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.FilterValue
import love.forte.simbot.action.sendIfSupport
import love.forte.simbot.event.FriendMessageEvent
import org.springframework.stereotype.Component
import per.zsck.simbot.common.annotation.RobotListen
import per.zsck.simbot.http.academic.service.ClassMapService
import per.zsck.simbot.http.academic.service.ScheduleService
import per.zsck.simbot.http.academic.util.AcademicUtil
import java.sql.Date
import java.time.LocalDate

/**
 * @author zsck
 * @date   2022/11/10 - 8:30
 */

@Component
class AcademicListener(
    val scheduleService: ScheduleService,
    val classMapService: ClassMapService,
    val academicUtil: AcademicUtil
){

    @RobotListen
    @Filter("{{index,\\d{1,2}}}")
    suspend fun FriendMessageEvent.viewWeek(@FilterValue("index")index: Long ){

        scheduleService.getLessonsByWeek(index).apply {
            academicUtil.getCourseDetailMsg(this).forEach { sendIfSupport(it) }
        }
    }

    @RobotListen
    @Filter("(w|W){{param,(\\+|-|=)?}}")
    suspend fun FriendMessageEvent.week(@FilterValue("param")param: String){
        val firstDate = scheduleService.getFirstDate()
        val date = Date.valueOf(DateUtil.today())
        val standard = getBalanceByParam(param)

        val gap: Long = DateUtil.between(firstDate, date, DateUnit.WEEK, false) + standard.value

        if (gap >= 0) {
            sendIfSupport("${standard.week} 为第 $gap 周")
        } else {
            sendIfSupport("${standard.week} 尚未开学,距离开学还有 ${DateUtil.between(
                date, firstDate, DateUnit.DAY, true)} 天")
            return
        }

        val scheduleList = scheduleService.getLessonsByWeek(gap)
        if (scheduleList.isEmpty()){
            sendIfSupport("${standard.week} 无课程")
            return
        }

        academicUtil.getCourseDetailMsg(scheduleList).forEach{
            sendIfSupport(it)
        }

    }
    @RobotListen
    @Filter("(d|D){{param,(\\+|-|=)?}}")
    suspend fun FriendMessageEvent.day(@FilterValue("param")param: String){
        val firstDate = scheduleService.getFirstDate()
        val standard = getBalanceByParam(param)
        val date = standard.getDateIfDay()

        if (date.before(firstDate)) {
            sendIfSupport("该日正处于假期，距离开学:${DateUtil.between(date, firstDate, DateUnit.DAY, true)}天")
        } else {
            val gap = DateUtil.between(firstDate, date, DateUnit.DAY, true)
            val scheduleList = scheduleService.getLessonsByDate(date)
            sendIfSupport("${standard.day} 日期: $date ,属于第 ${gap / 7 + 1} 周")
            if (scheduleList.isEmpty()) {
                sendIfSupport("当日无课程")
                return
            }
            academicUtil.getCourseDetailMsg(scheduleList).forEach {
                sendIfSupport(it)
            }
        }
    }
    @RobotListen
    @Filter("(f|F)\\s*{{name}}")
    suspend fun FriendMessageEvent.find(@FilterValue("name")name: String){
        val classMapList = classMapService.likeClassName(name)

        if (classMapList.isEmpty()){
            sendIfSupport("未查询到符合条件的信息")
        }else{
            for (classMap in classMapList) {
                val classDetail = scheduleService.getClassDetail(classMap.id!!)

                sendIfSupport(academicUtil.getLessonInfoMsg(classMap, classDetail))
            }
        }
    }


    fun getBalanceByParam(param: String): Standard {
        return when (param) {
            "+" -> {
                Standard.NEXT
            }
            "-" -> {
                Standard.LAST
            }
            else -> Standard.THIS
        }
    }

    enum class Standard(val value: Int, val week: String, val day: String) {
        //今天、明天、昨天 或 本周、下周、上周
        THIS(1, "本周", "今天"), NEXT(2, "下周", "明天"), LAST(0, "上周", "昨天");

        fun getDateIfDay(): Date{

            return Date.valueOf(LocalDate.now().let {
                if (value > 1) it.plusDays(1)
                else if (value < 1) it.minusDays(1)
                else it
            })
        }
    }
}