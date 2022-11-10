package per.zsck.simbot.core.state

import org.springframework.stereotype.Component
import per.zsck.simbot.core.state.entity.GroupState
import per.zsck.simbot.core.state.service.GroupStateService
import javax.annotation.PostConstruct

/**
 * @author zsck
 * @date   2022/11/5 - 14:22
 */
@Component
class GroupStateCache(
    val groupStateService: GroupStateService
) {

    companion object{
        val STATE_MAP = mutableMapOf<String, GroupStateEnum>()
    }
    @PostConstruct
    fun init(){
        groupStateService.list().forEach{
            groupState -> STATE_MAP[groupState.groupNumber!!] = groupState.state
        }
    }

    fun setGroupState(group: String, stateEnum: GroupStateEnum): Boolean{
        val state = STATE_MAP.computeIfAbsent(group) { return@computeIfAbsent GroupStateEnum.CLOSED }
        return if (stateEnum == state){
            false
        }else{
            STATE_MAP[group] = stateEnum
            true
        }
    }

}