package per.zsck.simbot.entertainment.xml

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import per.zsck.custom.util.jackson.JacksonUtil

/**
 * @author zsck
 * @date   2023/2/12 - 16:08
 */
object AppMsgHelper {

    private const val APP_MSG_PATH = "entertainment/xml/app.json"
    private val appMsgStr: JsonNode = JacksonUtil.objectMapper.readTree(this::class.java.classLoader.getResourceAsStream(APP_MSG_PATH))

    fun getAppMsg(appMsg: AppMsg): JsonNode {
        return (appMsgStr as ObjectNode).apply {
            this.put("prompt", appMsg.prompt)
            this["meta"]["notification"].let {
                it["appInfo"].apply {
                    (this as ObjectNode).put("appName", appMsg.appName)
                    this.put("iconUrl", appMsg.iconUrl)
                }

                it["data"][0].apply {
                    (this as ObjectNode).put("title", appMsg.title)
                    this.put("value", appMsg.value)
                }
            }
        }
    }
}