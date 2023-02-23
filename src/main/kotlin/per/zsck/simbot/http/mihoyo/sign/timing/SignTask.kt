package per.zsck.simbot.http.mihoyo.sign.timing

import kotlinx.coroutines.runBlocking
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.mirai.message.MiraiForwardMessageBuilder
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import per.zsck.simbot.core.config.MiraiBotManagerSupport
import per.zsck.simbot.core.state.enums.GenshinSignPushEnum
import per.zsck.simbot.core.state.service.GroupStateService
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
    val groupStateService: GroupStateService
        ): MiraiBotManagerSupport(){


    @Scheduled(cron = "00 00 10 * * ?")
    fun sign(){

        val list = genshinInfoService.list()

        val msg = MiraiForwardMessageBuilder().apply {
            list.forEach { info ->
                genShinSign.doSignWithAward(info).let {

                    this.add(miraiBot.id, "米游社签到", Timestamp.now(), genShinSign.getResMsg(info, it))

                }
            }
        }
        groupStateService.getGroupStateByState(GenshinSignPushEnum.NORMAL).forEach {

            runBlocking { it.groupNumber?.let { number -> miraiBot.group( number.ID )?.send(msg.build()) } }

        }

    }

}