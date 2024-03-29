package per.zsck.simbot.http.mihoyo.sign.listener


import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import love.forte.simboot.annotation.Filter
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.Timestamp
import love.forte.simbot.component.mirai.message.MiraiForwardMessageBuilder
import love.forte.simbot.event.*
import love.forte.simbot.message.*
import net.mamoe.mirai.message.data.ForwardMessage
import net.mamoe.mirai.message.data.RawForwardMessage
import org.springframework.stereotype.Component
import per.zsck.simbot.common.annotation.RobotListen
import per.zsck.simbot.common.logInfo
import per.zsck.simbot.http.mihoyo.sign.GenShinSign
import per.zsck.simbot.http.mihoyo.sign.entity.GenshinInfo
import per.zsck.simbot.http.mihoyo.sign.service.GenshinInfoService
import kotlin.time.Duration.Companion.seconds

/**
 * @author zsck
 * @date   2022/11/3 - 22:21
 */
@Suppress("unused")
@Component
class GenShinSignListener(
    val genShinSign: GenShinSign,
    val genshinInfoService: GenshinInfoService
) {


    @RobotListen
    @Filter("/签到")
    suspend fun GroupMessageEvent.genshinSign(){
        val author = author()
        val group = this.group()

        MiraiForwardMessageBuilder().apply {
            genshinInfoService.getGenshinInfoList(author.id.toString()).let {



                it.onEach { info->

                    val numberOfSign = genShinSign.doSignWithAward(info)

                    this.add(author.id, "米游社签到", Timestamp.now(), genShinSign.getResMsg(info, numberOfSign) )

                }

                if ( it.isNotEmpty() ){
                    this.displayStrategy = GenshinSignDisplayStrategy

                    group.send(this.build())
                }else{
                    group.send( buildMessages {
                        this.append( At(author.id) ).append( " 你当前尚未绑定原神账号哦" )
                    } )
                }

            }

        }
    }

    @OptIn(ExperimentalSimbotApi::class)
    @RobotListen
    @Filter("/绑定原神账号")
    suspend fun GroupMessageEvent.bindGenshin(sessionContext: ContinuousSessionContext ): EventResult{

        val author = author()

        group().send( buildMessages {
            + At(author.id)
            + "请在私聊中完成后续操作"
        } )

        author.send("请发送需要绑定的原神账号的cookie")

        val nextMessage = try {
            withTimeout(120.seconds){

                sessionContext.waitingForNextMessage(FriendMessageEvent) { event -> event.source().id == author.id }

            }
        }catch (e: TimeoutCancellationException){
            author.send("会话因超时(120s)自动关闭")
            return EventResult.invalid()
        }


        val genshinInfos = genShinSign.analyzeCookie(nextMessage.plainText)

        if (genshinInfos.isNotEmpty() ){
            val desInfo = if (genshinInfos.size == 1){  genshinInfos[0]  }else{
                //cookie获取的原神账号信息不唯一,需要进一步确认
                author.send("cookie对应的原神账号如下,请发送需要绑定的原神账号的uid")
                author.send(MessagesBuilder().apply { //发送提示信息,列出可选uid
                    genshinInfos.forEach{
                        this.append("uid: ${it.uid}, 昵称: ${it.nickName}\n")
                } }.build())

                try {//try 语句的返回指即为该else分值的返回值
                    //超时处理,默认会话时长为120秒
                    withTimeout(120.seconds) { //开启持续会话,进一步确认uid

                        sessionContext.waiting { provider ->
                            val currentEvent = this.event

                            if (currentEvent is FriendMessageEvent) {
                                val provideUid = currentEvent.messageContent.plainText

                                //输入的uid必须有对应的info在genshinInfos中,find结果不为null则push,否则提醒用户重新输入
                                genshinInfos.find { info -> info.uid == provideUid }?.apply {
                                        provider.push(this)
                                } ?: author.send("cookie中并不包含指定的原神账号(uid: ${provideUid})的信息,请重新输入")

                            }
                        }
                    }
                }catch (e: TimeoutCancellationException){
                    author.send("会话因超时(120s)自动关闭")
                    return EventResult.invalid()
                }
            }

            if (genshinInfoService.saveGenshinInfo(desInfo, author.id.toString())){

                logInfo("用户[{}] 与原神账号[uid: {}]成功绑定", author.id, desInfo.uid)
                author.send("成功绑定原神账号\n uid: ${desInfo.uid}\n 昵称: ${desInfo.nickName}")

            }

        }else{//无法从cookie中获取原神账号信息
            author.send("无法从cookie中获取原神账号信息,请确认cookie有效性")
        }

        return EventResult.truncate()
    }

    @OptIn(ExperimentalSimbotApi::class)
    @RobotListen
    @Filter("/解绑原神账号")
    suspend fun GroupMessageEvent.unbindGenshin(sessionContext: ContinuousSessionContext ): EventResult{
        val author = author()
        val group = this.group()

        group.send( buildMessages {
            + At(author.id)
            + "请在私聊中完成后续操作"
        })

        val genshinInfoList = genshinInfoService.getGenshinInfoList(author.id.toString())

        if (genshinInfoList.isNotEmpty()){

            author.send(MessagesBuilder().apply {

                genshinInfoList.forEach { this.append("uid: ${it.uid}, 昵称: ${it.nickName}\n") }

            }.build())

            author.send("请发送需要解绑的原神账号的uid")
            val desUid = try {
                withTimeout(120.seconds){

                    sessionContext.waitingForNextMessage(FriendMessageEvent) { event ->
                        if (event.source().id == author.id) {
                            val provideUid = event.messageContent.plainText

                            //genshinInfoList中有uid相同的账号才可
                            return@waitingForNextMessage genshinInfoList.contains( GenshinInfo(provideUid) ).apply {
                                if (!this)  author.send("该账号尚未绑定uid为 $provideUid 的原神账号")
                            }
                        }
                        return@waitingForNextMessage false
                    }.plainText

                }
            }catch (e: TimeoutCancellationException){
                author.send("会话因超时(120s)自动关闭")
                return EventResult.invalid()
            }

            if ( genshinInfoService.removeGenshinInfo(desUid) ){
                author.send("成功于账号 uid: $desUid 解除绑定")

                logInfo("用户[{}] 与原神账号[uid: {}]解除绑定", author.id.toString(), desUid)
            }

        }else{

            author.send("当前尚未绑定原神账号")
        }

        return EventResult.truncate()
    }

    object GenshinSignDisplayStrategy: ForwardMessage.DisplayStrategy{
        override fun generatePreview(forward: RawForwardMessage): List<String> =
            forward.nodeList.map { "米游社签到: " + it.messageChain.contentToString() }

        override fun generateSummary(forward: RawForwardMessage): String = "查看${forward.nodeList.size}条签到记录"

        override fun generateTitle(forward: RawForwardMessage): String = "米游社签到详情"
    }
}