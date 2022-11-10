package per.zsck.simbot.http.academic.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.IService
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service
import per.zsck.simbot.http.academic.entity.Schedule
import per.zsck.simbot.http.academic.mapper.ScheduleMapper
import java.sql.Date

/**
 * @author zsck
 * @date   2022/11/8 - 14:51
 */

interface ScheduleService: IService<Schedule> {
    fun removeAll(): Long

    fun getLessonsByWeek(week: Long): List<Schedule>

    fun getLessonsByDate(date: Date): List<Schedule>

    fun getFirstDate(): Date

    fun getClassDetail(lessonId: Int): Map<String, Any>
}

@Service
class ScheduleServiceImpl: ScheduleService, ServiceImpl<ScheduleMapper, Schedule>(){
    override fun removeAll() : Long {
        return count().also { remove(null) }
    }

    override fun getLessonsByWeek(week: Long): List<Schedule> {
        return list(KtQueryWrapper(Schedule::class.java)
            .eq(Schedule::weekIndex, week)
            .orderByAsc(Schedule::date)
            .orderByAsc(Schedule::startTime))
    }

    override fun getLessonsByDate(date: Date): List<Schedule> {
        return list(KtQueryWrapper(Schedule::class.java)
            .eq(Schedule::date, date)
            .orderByAsc(Schedule::startTime))
    }

    override fun getFirstDate(): Date {
        return getMap(QueryWrapper<Schedule>().select("MIN(date) as date"))["date"] as Date
    }

    override fun getClassDetail(lessonId: Int): Map<String, Any> {
        return baseMapper.getClassDetail(lessonId)
    }

}