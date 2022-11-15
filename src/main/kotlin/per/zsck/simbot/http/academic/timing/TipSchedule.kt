package per.zsck.simbot.http.academic.timing

import cn.hutool.core.date.DateUnit
import cn.hutool.core.date.DateUtil
import kotlinx.coroutines.runBlocking
import love.forte.simbot.ID
import love.forte.simbot.action.sendIfSupport
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import per.zsck.simbot.common.logInfo
import per.zsck.simbot.core.config.MiraiBotManagerSupport
import per.zsck.simbot.http.academic.entity.Schedule
import per.zsck.simbot.http.academic.service.ScheduleService
import per.zsck.simbot.http.academic.util.AcademicUtil
import java.sql.Date
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

/**
 * @author zsck
 * @date   2022/11/15 - 18:23
 */
@EnableScheduling
@Component
class TipSchedule  (
    val scheduleService: ScheduleService,
    val academicUtil: AcademicUtil
): MiraiBotManagerSupport(){

    @Value("\${zsck.permit.owner}")
    lateinit var botHost: String

    lateinit var scheduledExecutorService: ScheduledExecutorService

    @PostConstruct
    fun init(){
        scheduledExecutorService = Executors.newScheduledThreadPool(5)
    }


    @Scheduled(cron = "0 40 7 * * 1-5")
    @Async
    fun morning() {
        val firstDate = scheduleService.getFirstDate()
        val date = DateUtil.date().toSqlDate()

        runBlocking {
            miraiBot.friend(botHost.ID)?.apply {
                val gap = DateUtil.between(firstDate, date, DateUnit.DAY, true)

                if (date.before(firstDate)) {
                    sendIfSupport("当前正处于假期，距离开学还有${gap}天")
                } else {

                    val scheduleList: List<Schedule> = scheduleService.getLessonsByDate(date)
                    if (scheduleList.isEmpty()) {
                        return@runBlocking
                    }
                    if (scheduleList[0].startTime != 800) {
                        if (LocalDateTime.now().hour <= 8) {
                            scheduledExecutorService.schedule({
                                logInfo("不是早八,延迟提醒")
                                morning()
                            }, 2, TimeUnit.HOURS)
                            return@runBlocking
                        }
                    }
                    sendIfSupport("今日为:${DateUtil.format(date, "MM-dd")} , 第:${(gap / 7 + 1)}周")
                    academicUtil.getCourseDetailMsg(scheduleList).forEach {
                        sendIfSupport(it)
                    }
                }
            }
        }

    }


    @Scheduled(cron = "0 0 22 * * 1,2,3,4,7")
    @Async
    fun even() {
        val firstDate: Date = scheduleService.getFirstDate()
        val date = DateUtil.tomorrow().toJdkDate()
        if (!date.before(firstDate)) { //开学之后
            val gap = DateUtil.between(firstDate, date, DateUnit.DAY, true)

            val scheduleList = scheduleService.getLessonsByDate(DateUtil.date(date).toSqlDate())
            if (scheduleList.isEmpty()) {
                return // 第二天没课则不提醒
            }

            runBlocking {
                miraiBot.friend(botHost.ID)?.apply {
                    sendIfSupport("明日为:${ DateUtil.format(date, "MM-dd") }, 第:${(gap / 7 + 1)}周")
                    academicUtil.getCourseDetailMsg(scheduleList).forEach {
                        sendIfSupport(it)
                    }
                }
            }

        }
    }
}