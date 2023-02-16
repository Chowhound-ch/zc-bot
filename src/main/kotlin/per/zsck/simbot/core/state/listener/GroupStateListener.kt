package per.zsck.simbot.core.state.listener

import cn.hutool.core.util.ReUtil
import love.forte.simboot.annotation.AnnotationEventFilterFactory
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.FilterValue
import love.forte.simboot.annotation.Filters
import love.forte.simboot.core.listener.BootListenerAttributes.rawFunction
import love.forte.simbot.MutableAttributeMap
import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.*
import org.springframework.stereotype.Component
import per.zsck.simbot.common.annotation.RobotListen
import per.zsck.simbot.common.utils.MessageUtil.groupNumber
import per.zsck.simbot.core.permit.Permit
import per.zsck.simbot.core.state.GroupStateCache
import per.zsck.simbot.core.state.enums.EnumUtil
import per.zsck.simbot.core.state.enums.GroupStateEnum
import per.zsck.simbot.core.state.service.GroupStateService

/**
 * @author zsck
 * @date   2022/11/6 - 12:27
 */
@Suppress("unused")
@Component
class GroupStateListener(
    val groupStateService: GroupStateService
){


    @RobotListen(stateLeast = GroupStateEnum.CLOSED, permission = Permit.MANAGER, priority = PriorityConstant.FIRST)
    @Filter("/开机状态{{desState,[0-2]}}")
    suspend fun GroupMessageEvent.setGroupState(@FilterValue("desState")desState: Int): EventResult{
        val desEnum = EnumUtil.getInstance<GroupStateEnum>(desState)
        val groupNumber = groupNumber()

        this.group().apply {
            val groupState = groupStateService.getGroupState(groupNumber)

            if (groupState.state != desEnum) {
                groupState.state = desEnum!!

                groupStateService.setGroupState(groupState)
                send("成功将群${groupNumber}设置为${desEnum}")
            }else{
                send("设置失败,群${groupNumber},已是${desEnum}状态")
            }
        }

        return EventResult.truncate()
    }


    @RobotListen( permission = Permit.MANAGER)
    @Filter( by = BoolEventFilter::class)
    suspend fun GroupMessageEvent.setBoolEvent(): EventResult{
        val msg = this.messageContent.plainText.substring(1)
        val boundary = msg.indexOfFirst { it.isDigit() }
        val desEvent = msg.substring(0, boundary).trim()

        val desState = GroupStateCache.getClassByFieldName(desEvent)?.let { EnumUtil.getInstance(it, msg.substring(boundary).toInt()) }
        val groupNumber = groupNumber()

        val groupState = groupStateService.getGroupState(groupNumber)
        desState?.let {
            val field = GroupStateCache.getByClass(desState::class.java)

            if (field.get(groupState) == desState) {
                this.group().send("群${groupNumber},已是${desState}状态")
            }else{
                field.set(groupState, desState)
                groupStateService.setGroupState(groupState)
                this.group().send("成功将群${groupNumber}设置为${desState}")
            }

        }
        return EventResult.truncate()
    }

}

class BoolEventFilter: AnnotationEventFilterFactory{
    override fun resolveFilter(
        listener: EventListener,
        listenerAttributes: MutableAttributeMap,
        filter: Filter,
        filters: Filters
    ): EventFilter {


        return object : EventFilter {
            override suspend fun test(context: EventListenerProcessingContext): Boolean {
                val desEvent = ReUtil.getGroup1("/([a-zA-Z]+)\\s*[0-1]", context.textContent) ?: return false
                context.listener.rawFunction
                GroupStateCache.ENUM_MAP.forEach{
                    if (desEvent.uppercase() == it.value.name.uppercase()){
                        return true
                    }
                }
                return false
            }
        }
    }

}