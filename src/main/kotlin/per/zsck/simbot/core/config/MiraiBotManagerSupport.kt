package per.zsck.simbot.core.config

import cn.hutool.extra.spring.SpringUtil
import love.forte.simbot.bot.OriginBotManager
import love.forte.simbot.component.mirai.bot.MiraiBot
import love.forte.simbot.component.mirai.bot.MiraiBotManager
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner

/**
 * 继承此类即可直接使用miraiBotManager
 * @author zsck
 * @date   2022/11/14 - 22:35
 */
abstract class MiraiBotManagerSupport : ApplicationRunner{

    lateinit var miraiBotManager: MiraiBotManager

    lateinit var miraiBot: MiraiBot

    override fun run(args: ApplicationArguments?) {
        miraiBotManager = SpringUtil.getBean(MiraiBotManager::class.java)

        miraiBot = miraiBotManager.all()[0]
    }
}