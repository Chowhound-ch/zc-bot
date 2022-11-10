package per.zsck.simbot.http.academic.entity

import cn.hutool.core.date.DateUnit
import cn.hutool.core.date.DateUtil
import com.baomidou.mybatisplus.annotation.TableField
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.sql.Date
import java.time.Instant

/**
 * @author zsck
 * @date   2022/11/8 - 14:41
 */
@JsonIgnoreProperties("room")
data class Schedule(
    @JsonProperty("lessonId")
    var lessonId: Int?,
    @JsonProperty("scheduleGroupId")
    var scheduleGroupId: Int?,
    var periods: Int?,
    var date: Date,
    var room: String,
    @TableField("week_day")
    var weekday: Int,
    @JsonProperty("startTime")
    var startTime: Int,
    @JsonProperty("endTime")
    var endTime: Int,
    @JsonProperty("personName")
    var personName: String,
    @JsonProperty("weekIndex")
    var weekIndex: Int
) {
    constructor(): this(null, null, null, Date(System.currentTimeMillis()), "xx学堂xx", 0, 800, 950, "未知", 0)
}