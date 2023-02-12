package per.zsck.simbot.entertainment.xml

import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.FilterValue
import love.forte.simboot.filter.MatchType
import love.forte.simbot.component.mirai.event.MiraiReceivedMessageContent
import love.forte.simbot.component.mirai.message.MiraiImage
import love.forte.simbot.component.mirai.message.MiraiMessageContent
import love.forte.simbot.component.mirai.message.SimbotOriginalMiraiMessage
import love.forte.simbot.event.GroupMessageEvent
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.utils.item.SingleValueItems
import net.mamoe.mirai.internal.deps.io.ktor.http.ContentType
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.LightApp
import org.springframework.stereotype.Component
import per.zsck.custom.util.jackson.JacksonUtil
import per.zsck.simbot.common.annotation.RobotListen
import javax.annotation.PostConstruct

/**
 * @author zsck
 * @date   2023/2/12 - 13:34
 */
@Component
class XmlListener {

    // /appmsg 通知内容:本群炼铜氛围浓厚，获得无忧官方的认可\n\n\n炼铜大师无忧认证\n2023年02月12日&炼铜大师无忧官方通知&该群已获得无忧认证&image
    @RobotListen(desc = "xml测试")
    @Filter("/appmsg\\s+{{msg,.+}}")
    suspend fun GroupMessageEvent.appMsg(@FilterValue("msg") msg: String) {
        val appMsg = AppMsg()

        // 设置appMsg的文本部分内容
        msg.split(':').apply {
            var mainValue = this[0] //本群炼铜氛围浓厚，获得无忧官方的认可\n\n\n炼铜大师无忧认证\n2023年02月12日&炼铜大师无忧官方通知&该群已获得无忧认证&image
            if (this.size > 1){
                appMsg.title = this[0] //通知内容
                mainValue = this[1]
            }

            mainValue.split('&').apply {
                appMsg.value = this[0] //本群炼铜氛围浓厚，获得无忧官方的认可\n\n\n炼铜大师无忧认证\n2023年02月12日
                if (this.size > 1){
                    appMsg.appName = this[1] //炼铜大师无忧官方通知
                }
                if (this.size > 2){
                    appMsg.prompt = this[2] //该群已获得无忧认证
                }
            }
        }

        if (this.messageContent.messages.size > 1 && this.messageContent.messages[1] is MiraiImage) {
            val image = this.messageContent.messages[1] as MiraiImage
            appMsg.iconUrl = image.queryUrl()
        }

        // 将appMsg.value中的'\','n'替换为'\n'
        appMsg.value = appMsg.value.replace("\\n", "\n")


        this.group().send(
            SimbotOriginalMiraiMessage(LightApp(
                JacksonUtil.toJsonString(AppMsgHelper.getAppMsg(appMsg))
            ))
        )
    }
}