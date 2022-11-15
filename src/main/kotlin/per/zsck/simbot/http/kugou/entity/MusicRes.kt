package per.zsck.simbot.http.kugou.entity

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author zsck
 * @date   2022/11/10 - 12:41
 */
data class MusicRes(
    var showTips: String? = null,
    @JsonProperty("songname")
    val songName: String? = null,
    val authorName: String? = null,
    @JsonProperty("data")
    val music: Music? = null,
)