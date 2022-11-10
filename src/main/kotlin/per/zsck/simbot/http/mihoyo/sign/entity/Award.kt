package per.zsck.simbot.http.mihoyo.sign.entity

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author zsck
 * @date   2022/11/3 - 14:53
 */
data class Award(
    val icon: String,
    val name: String,
    @JsonProperty("cnt")
    val number: Int
)