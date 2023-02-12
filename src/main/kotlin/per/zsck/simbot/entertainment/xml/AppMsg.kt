package per.zsck.simbot.entertainment.xml

/**
 * @author zsck
 * @date   2023/2/12 - 16:48
 */
data class AppMsg(
    var prompt: String = "紧急通知",
    var appName: String = "腾讯官方通知",
    var iconUrl: String = "",
    var title: String = "通知内容",
    var value: String = "主体内容",
)