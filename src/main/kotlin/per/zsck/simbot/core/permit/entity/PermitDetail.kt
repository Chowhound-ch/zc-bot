package per.zsck.simbot.core.permit.entity

import per.zsck.simbot.core.permit.Permit

/**
 * @author zsck
 * @date   2022/11/5 - 13:26
 */
data class PermitDetail(
    val id: Long?,
    val qqNumber: String?,
    val permit: Permit
){
    constructor(): this(null, null, Permit.MANAGER)

    constructor(qqNumber: String?): this(null, qqNumber, Permit.MANAGER)
}
