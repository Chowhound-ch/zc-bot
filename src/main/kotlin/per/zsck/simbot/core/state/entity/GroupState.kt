package per.zsck.simbot.core.state.entity

import per.zsck.simbot.core.state.GroupStateEnum

/**
 * @author zsck
 * @date   2022/11/5 - 11:14
 */
data class GroupState(
    var id: Long?,
    var groupNumber: String?,
    var state: GroupStateEnum,
    var lessonPush: Int
) {
    constructor(groupNumber: String? = null, state: GroupStateEnum = GroupStateEnum.CLOSED): this(null, groupNumber, state, 0)

}