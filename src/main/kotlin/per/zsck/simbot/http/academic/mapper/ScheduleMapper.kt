package per.zsck.simbot.http.academic.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import per.zsck.simbot.http.academic.entity.Schedule

/**
 * @author zsck
 * @date   2022/11/8 - 14:50
 */
@Mapper
interface ScheduleMapper : BaseMapper<Schedule>{
    @Select("select MAX(week_index) as end ,MIN(week_index) as start ,person_name from schedule where lesson_id = #{lessonId} group by person_name")
    fun getClassDetail(@Param("lessonId") lessonId: Int): Map<String, Any>
}