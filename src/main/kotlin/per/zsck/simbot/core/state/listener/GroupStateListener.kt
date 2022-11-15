package per.zsck.simbot.core.state.listener

import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.FilterValue
import love.forte.simbot.PriorityConstant
import love.forte.simbot.action.sendIfSupport
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.GroupMessageEvent

import org.springframework.stereotype.Component
import per.zsck.simbot.common.annotation.RobotListen
import per.zsck.simbot.common.utils.MessageUtil.groupNumber
import per.zsck.simbot.core.permit.Permit
import per.zsck.simbot.core.state.GroupStateCache
import per.zsck.simbot.core.state.GroupStateEnum
import per.zsck.simbot.core.state.service.GroupStateService

/**
 * @author zsck
 * @date   2022/11/6 - 12:27
 */
@Component
class GroupStateListener(
    val groupStateService: GroupStateService
){
    @RobotListen(stateLeast = GroupStateEnum.CLOSED, permission = Permit.MANAGER, priority = PriorityConstant.FIRST)
    @Filter("/开机状态{{desState,[0-2]}}")
    suspend fun GroupMessageEvent.setGroupState(@FilterValue("desState")desState: Int): EventResult{
        val desEnum = GroupStateEnum.getInstance(desState)
        val groupNumber = groupNumber()

        if (groupStateService.setGroupStateAndCache(groupNumber, desEnum)) {

            sendIfSupport("成功将群${groupNumber}设置为${desEnum}")
        }else{
            sendIfSupport("设置失败,群${groupNumber},已是${desEnum}状态")
        }
        return EventResult.truncate()
    }
}