package per.zsck.simbot.common.utils

import love.forte.simbot.event.GroupEvent
import love.forte.simbot.resources.URLResource
import java.net.URL

/**
 * @author zsck
 * @date   2022/11/5 - 13:09
 */
object MessageUtil {

    suspend fun GroupEvent.groupNumber(): String {
        return group().id.toString()
    }

    fun String.toURLResource() = URLResource(URL(this))

}