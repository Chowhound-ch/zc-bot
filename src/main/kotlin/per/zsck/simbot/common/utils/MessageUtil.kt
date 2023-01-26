package per.zsck.simbot.common.utils

import love.forte.simbot.event.GroupEvent

/**
 * @author zsck
 * @date   2022/11/5 - 13:09
 */
object MessageUtil {

    suspend fun GroupEvent.groupNumber(): String {
        return group().id.toString()
    }

}