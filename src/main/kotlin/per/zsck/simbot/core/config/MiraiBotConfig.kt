package per.zsck.simbot.core.config

import cn.hutool.extra.spring.SpringUtil
import love.forte.simbot.FragileSimbotApi
import love.forte.simbot.Simbot
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.OriginBotManager
import love.forte.simbot.component.mirai.bot.MiraiBot
import love.forte.simbot.component.mirai.bot.MiraiBotManager
import love.forte.simbot.component.mirai.bot.firstMiraiBotManager
import love.forte.simbot.component.mirai.bot.miraiBotManagers
import love.forte.simbot.component.mirai.event.MiraiBotRegisteredEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import per.zsck.simbot.common.annotation.RobotListen
import org.springframework.context.annotation.Lazy

/**
 * @author zsck
 * @date   2022/11/14 - 15:02
 */
@Lazy
@Configuration
class MiraiBotConfig {

    lateinit var applicationContext: ApplicationContext

    @RobotListen
    suspend fun MiraiBotRegisteredEvent.getBotManager(){
        SpringUtil.registerBean("miraiBotManager", bot.manager)
    }


}