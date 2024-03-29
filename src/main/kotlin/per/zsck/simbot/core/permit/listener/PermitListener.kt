package per.zsck.simbot.core.permit.listener

import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.FilterValue
import love.forte.simboot.filter.MatchType
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.GroupMessageEvent
import love.forte.simbot.message.At
import org.springframework.stereotype.Component
import per.zsck.simbot.common.annotation.RobotListen
import per.zsck.simbot.core.permit.Permit
import per.zsck.simbot.core.permit.service.PermitDetailService
import java.util.*

/**
 * @author zsck
 * @date   2022/11/6 - 15:28
 */
@Suppress("unused")
@Component
class PermitListener (
    val permitDetailService: PermitDetailService
        ) {

    @RobotListen(permission = Permit.HOST)
    @Filter("/设置权限{{desPermit,\\w+}}", matchType = MatchType.REGEX_CONTAINS)
    suspend fun GroupMessageEvent.setPermit(@FilterValue("desPermit")desPermit: String): EventResult{
        val permit = try {
            Permit.valueOf(desPermit.uppercase())
        } catch (e: IllegalArgumentException) {
            return EventResult.invalid()
        }

        messageContent.messages[1].let {
            if (it is At){
                val target = it.target.toString()
                this.group().apply {
                    if (permitDetailService.setPermit(target, permit)) {
                        send("成功将${target}权限设置为${desPermit}")
                    }else{
                        send("设置失败,${target}的权限已是${desPermit}")
                    }
                }

            }
        }


        return EventResult.truncate()
    }
}