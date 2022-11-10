package per.zsck.simbot.core.state.entity

import per.zsck.simbot.core.state.GroupStateEnum

/**
 * @author zsck
 * @date   2022/11/5 - 11:14
 */
data class GroupState(
    var id: Long?,
    var groupNumber: String?,
    var state: GroupStateEnum
) {
    constructor(): this(null, null, GroupStateEnum.CLOSED)
}