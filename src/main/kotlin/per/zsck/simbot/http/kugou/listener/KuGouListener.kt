package per.zsck.simbot.http.kugou.listener

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.FilterValue
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.action.sendIfSupport
import love.forte.simbot.component.mirai.message.asSimbotMessage
import love.forte.simbot.event.*
import love.forte.simbot.message.Message
import love.forte.simbot.message.buildMessages
import net.mamoe.mirai.message.data.MusicKind
import net.mamoe.mirai.message.data.MusicShare
import org.springframework.stereotype.Component
import per.zsck.simbot.common.annotation.RobotListen
import per.zsck.simbot.http.kugou.KuGouMusic
import per.zsck.simbot.http.kugou.entity.Music
import per.zsck.simbot.http.kugou.service.MusicService
import kotlin.time.Duration.Companion.seconds

/**
 * @author zsck
 * @date   2022/11/10 - 14:21
 */
@Suppress("unused")
@Component
class KuGouListener(
    val kuGouMusic: KuGouMusic,
    val musicService: MusicService
) {



    @OptIn(ExperimentalSimbotApi::class)
    @RobotListen
    @Filter("^/点歌\\s*{{param,(-d|D)?}}\\s*{{keyword}}")
    suspend fun GroupMessageEvent.requestSong(@FilterValue("param") param: String,
                                               @FilterValue("keyword") keyword: String,
                                               sessionContext: ContinuousSessionContext): EventResult{

        val author = author()

        val searchRes = kuGouMusic.getSearchRes(keyword, if (param.isNotEmpty()) 8 else 1)
        lateinit var  desMusic: Music

        when (searchRes.size) {
            0 -> {
                sendIfSupport("未找到关键字为 $keyword 的歌曲")
                return EventResult.truncate()
            }
            1 -> {//如果目标音乐只有一首则直接分享该音乐

                desMusic = searchRes[0]

            }
            else -> {

                sendIfSupport(buildMessages {
                    for ( i in 0 until  searchRes.size) {
                        searchRes[i].let {
                            this.append("${i + 1} 、")
                            it.url?.let { this.append("(本地)") }
                        }
                        this.append(searchRes[i].audioName ?: "未知歌曲").append("\n")
                    }
                })

                val desIndex = try {
                    withTimeout(120.seconds){
                        sessionContext.waitingForNextMessage(GroupMessageEvent) { event ->
                            if (event.author().id == author.id) {
                                event.messageContent.plainText.toIntOrNull()?.let {
                                    it in 1 .. searchRes.size
                                } ?: false
                            } else {
                                false
                            }
                        }
                    }
                }catch (e: TimeoutCancellationException){
                    sendIfSupport("会话因超时(120s)自动关闭")
                    return EventResult.invalid()
                }
                desMusic = searchRes[desIndex.plainText.toInt() - 1]

            }
        }

        sendIfSupport(
            desMusic.let {

                if (it.url == null && it.fileHash != null){//如果该音乐不是从本地找到的则从网络获取
                    kuGouMusic.getMusicUrlByAlbumIDAndHash(it)
                }else{ it }

            }?.let {
                if (it.imgUrl == null ) it.imgUrl = author.avatar

                getMusicShare(it)
            } ?: buildMessages { this.append("未找到结果或歌曲为付费歌曲") })

        return EventResult.truncate()
    }



    @RobotListen
    @Filter("^/上传歌曲")
    suspend fun GroupMessageEvent.uploadMusic(){
        sendIfSupport("请访问网站")
    }

    fun getMusicShare(music: Music): Message.Element<*>? {
        return try {
            MusicShare(MusicKind.KugouMusic, music.title!!, music.audioName!!, music.url!!,
                music.imgUrl!!, music.url!!).asSimbotMessage()
        }catch (_: NullPointerException){ null }

    }
}