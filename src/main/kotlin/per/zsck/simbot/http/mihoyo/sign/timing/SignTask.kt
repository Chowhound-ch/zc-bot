package per.zsck.simbot.http.mihoyo.sign.timing

import kotlinx.coroutines.runBlocking
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.action.sendIfSupport
import love.forte.simbot.component.mirai.message.MiraiForwardMessageBuilder
import love.forte.simbot.message.buildMessages
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import per.zsck.simbot.core.config.MiraiBotManagerSupport
import per.zsck.simbot.core.state.GroupStateCache
import per.zsck.simbot.core.state.GroupStateEnum
import per.zsck.simbot.http.mihoyo.sign.GenShinSign
import per.zsck.simbot.http.mihoyo.sign.service.GenshinInfoService

/**
 * @author zsck
 * @date   2022/11/14 - 21:27
 */
@EnableScheduling
@Component
class SignTask (
    val genShinSign: GenShinSign,
    val genshinInfoService: GenshinInfoService,
    val groupStateCache: GroupStateCache
        ): MiraiBotManagerSupport(){


    @Scheduled(cron = "00 23 18 * * ?")
//    @Scheduled(cron = "00 00 10 * * ?")
    fun sign(){

        val list = genshinInfoService.list()


        val msg = MiraiForwardMessageBuilder().apply {
            list.forEach { info ->
                genShinSign.doSignWithAward(info).let {

                    this.add(miraiBot.id, "米游社签到", Timestamp.now(), genShinSign.getResMsg(info, it))

                }
            }
        }
        groupStateCache.getGroupsWithState(GroupStateEnum.OPENED_ALL).forEach {

            runBlocking { miraiBot.group( it.ID )?.sendIfSupport(msg.build()) }

        }

    }

}