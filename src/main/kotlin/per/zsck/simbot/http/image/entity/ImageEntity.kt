package per.zsck.simbot.http.image.entity

/**
 * @author zsck
 * @date   2023/2/15 - 15:36
 */
data class ImageEntity (
    var title: String = "未知",
    var r18: Boolean = false,
    var tags: List<String> = emptyList(),
    var urls: Urls
    )

data class Urls(var original: String = "")