package per.zsck.simbot.common.utils

import love.forte.simbot.event.Event
import love.forte.simbot.event.GroupEvent
import love.forte.simbot.event.GroupMessageEvent
import love.forte.simbot.event.MessageEvent

/**
 * @author zsck
 * @date   2022/11/5 - 13:09
 */
object MessageUtil {

    suspend fun GroupEvent.groupNumber(): String {
        return group().id.toString()
    }

}