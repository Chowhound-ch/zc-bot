package per.zsck.simbot.http.mihoyo.sign.entity

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author zsck
 * @date   2022/11/3 - 20:35
 */
data class SignDetail(
    var totalSignDay: Int,
    @JsonProperty("is_sign")
    var isSign: Boolean,
    var signCntMissed: Int
    )