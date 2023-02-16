package per.zsck.simbot.core.other

import kotlinx.coroutines.withTimeout
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.ID
import love.forte.simbot.event.ContinuousSessionContext
import love.forte.simbot.event.FriendAddRequestEvent
import love.forte.simbot.event.FriendMessageEvent
import love.forte.simbot.event.waitingForNextMessage
import love.forte.simbot.message.buildMessages
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import per.zsck.simbot.common.annotation.RobotListen
import per.zsck.simbot.core.config.MiraiBotManagerSupport
import kotlin.time.Duration.Companion.minutes

/**
 * @author zsck
 * @date   2023/2/16 - 15:13
 */
@Controller
class FriendAddListener: MiraiBotManagerSupport() {

    @Value("\${zsck.permit.owner}")
    lateinit var owner: String

    @OptIn(ExperimentalSimbotApi::class)
    @RobotListen
    suspend fun FriendAddRequestEvent.addFriend(sessionContext: ContinuousSessionContext){
        val userInfo = this.requester()
        val host = miraiBot.friend(owner.ID) ?: run{ this.reject() ; return }
        val requestEvent = this

        host.send( buildMessages {
            this.append( "有人添加我为好友了\n" )
            this.append( "昵称: ${userInfo.username}\n" )
            this.append( "QQ: ${userInfo.id}\n" )
            requestEvent.message?.let { this.append( "验证信息: $it\n" ) }
            this.append( "请问是否同意添加好友?(Y/n)\n" )
        } )

        withTimeout(30.minutes){
            sessionContext.waitingForNextMessage(FriendMessageEvent){ event->
                val source = event.source()
                if (source.id == host.id){
                    val msg = event.messageContent.plainText
                    if (msg.uppercase() == "Y"){
                        requestEvent.accept()
                        host.send( buildMessages {
                            this.append( "已同意添加好友\n" )
                            this.append( "昵称: ${userInfo.username}\n" )
                            this.append( "QQ: ${userInfo.id}\n" )
                        } )
                        return@waitingForNextMessage true
                    }else if (msg.uppercase() == "N"){
                        requestEvent.reject()
                        host.send( buildMessages {
                            this.append( "已拒绝添加好友\n" )
                            this.append( "昵称: ${userInfo.username}\n" )
                            this.append( "QQ: ${userInfo.id}\n" )
                        } )
                        return@waitingForNextMessage true
                    }
                }

                return@waitingForNextMessage false
            }
        }

    }
}