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
import net.mamoe.mirai.message.data.FileMessage
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.LightApp
import org.springframework.stereotype.Component
import per.zsck.custom.util.jackson.JacksonUtil
import per.zsck.simbot.common.annotation.RobotListen
import per.zsck.simbot.core.state.entity.GroupState
import per.zsck.simbot.core.state.enums.CanHeartEnum
import per.zsck.simbot.core.state.enums.EnumUtil
import javax.annotation.PostConstruct

/**
 * @author zsck
 * @date   2023/2/12 - 13:34
 */
@Component
class XmlListener {

    // /appmsg 通知内容:本群炼铜氛围浓厚，获得无忧官方的认可\n\n\n炼铜大师无忧认证\n2023年02月12日&炼铜大师无忧官方通知&该群已获得无忧认证&image
    @RobotListen
    @Filter("/appmsg\\s+{{msg,[\\s\\S]+}}")
    suspend fun GroupMessageEvent.appMsg(@FilterValue("msg") msg: String) {
        this.messageContent.messages
        val appMsg = AppMsg()

        // 设置appMsg的文本部分内容
        msg.indexOfFirst { it == ':' || it == '：' }.let {

            if (it > 0){
                appMsg.title = msg.substring(0, it) //通知内容
                msg.substring(it + 1)
            }else{ msg } //本群炼铜氛围浓厚，获得无忧官方的认可\n\n\n炼铜大师无忧认证\n2023年02月12日&炼铜大师无忧官方通知&该群已获得无忧认证

                .split('&').apply {
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


    @RobotListen(boolEnumCondition = [CanHeartEnum::class])
    @Filter("/can\\s*can\\s*heart")
    suspend fun GroupMessageEvent.canCanHeart() {

        this.group().send(
            SimbotOriginalMiraiMessage(LightApp(
                "{\"app\":\"com.tencent.gamecenter.gameshare\",\"desc\":\"\",\"view\":\"noDataView\",\"ver\":\"0.0.0.0\",\"prompt\":\"让妲己看看你的心~\",\"appID\":\"\",\"sourceName\":\"\",\"actionData\":\"\",\"actionData_A\":\"\",\"sourceUrl\":\"\",\"meta\":{\"shareData\":{\"DATA10\":\"\",\"DATA13\":\"0\",\"DATA14\":\"videotest1\",\"jumpUrl\":\"http:\\/\\/game.gtimg.cn\\/images\\/yxzj\\/zlkdatasys\\/audios\\/audio\\/20220412\\/16497568317400.wav\",\"scene\":\"SCENE_SHARE_VIDEO\",\"type\":\"video\",\"url\":\"http:\\/\\/game.gtimg.cn\\/images\\/yxzj\\/zlkdatasys\\/audios\\/audio\\/20220412\\/16497568317400.wav\"}},\"config\":{\"ctime\":1674022176,\"forward\":0,\"height\":-1000,\"token\":\"61fe0a996f85e161b98fb748ff6f1209\",\"type\":\"normal\",\"width\":-1000},\"text\":\"\",\"sourceAd\":\"\",\"extra\":\"\"}"
            ))
        )
    }

//    @RobotListen
//    suspend fun GroupMessageEvent.canCanElse() {
//        val group1 = this.group()
//        group1.send(
//            SimbotOriginalMiraiMessage(LightApp(
//                "{\"app\":\"com.tencent.gamecenter.gameshare\",\"desc\":\"\",\"view\":\"noDataView\",\"ver\":\"0.0.0.0\",\"prompt\":\"让妲己看看你的心~\",\"appID\":\"\",\"sourceName\":\"\",\"actionData\":\"\",\"actionData_A\":\"\",\"sourceUrl\":\"\",\"meta\":{\"shareData\":{\"DATA10\":\"\",\"DATA13\":\"0\",\"DATA14\":\"videotest1\",\"jumpUrl\":\"http:\\/\\/game.gtimg.cn\\/images\\/yxzj\\/zlkdatasys\\/audios\\/audio\\/20220412\\/16497568317400.wav\",\"scene\":\"SCENE_SHARE_VIDEO\",\"type\":\"video\",\"url\":\"http:\\/\\/game.gtimg.cn\\/images\\/yxzj\\/zlkdatasys\\/audios\\/audio\\/20220412\\/16497568317400.wav\"}},\"config\":{\"ctime\":1674022176,\"forward\":0,\"height\":-1000,\"token\":\"61fe0a996f85e161b98fb748ff6f1209\",\"type\":\"normal\",\"width\":-1000},\"text\":\"\",\"sourceAd\":\"\",\"extra\":\"\"}"
//
//            ))
//        )
//    }




}