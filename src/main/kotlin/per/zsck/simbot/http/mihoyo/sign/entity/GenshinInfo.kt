package per.zsck.simbot.http.mihoyo.sign.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import per.zsck.simbot.http.mihoyo.sign.enums.ServerType

/**
 * @author zsck
 * @date   2022/10/31 - 20:10
 */
data class GenshinInfo(
    @TableId
    var id: Long?,
    var qqNumber: String?,
    var nickName: String,
    var cookie: String,
    var push: Int,
    var deletes: Int,
    @TableField(exist = false)
    var serverType: ServerType = ServerType.OFFICIAL
) {

    constructor(uid: String, nickName: String, cookie: String) : this(
        null, null, nickName, cookie, 0, 0
    ){
        this.uid = uid
    }
    constructor(uid: String) : this(){
        this.uid = uid
    }
    constructor() : this(
        null, null,"", "", 0, 0
    )

    var uid: String = "100"
        set(value) {
            this.serverType = if (value!![0] < '5') ServerType.OFFICIAL else ServerType.FOREIGN
            field = value
        }

    override fun equals(other: Any?): Boolean {
        return uid == other || other is GenshinInfo && uid == other.uid
    }
}