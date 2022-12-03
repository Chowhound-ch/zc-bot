package per.zsck.simbot.http.mihoyo.sign.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import per.zsck.simbot.http.mihoyo.sign.enums.ServerType

/**
 * @author zsck
 * @date   2022/12/2 - 8:20
 */
@Document("genshin_info")
class GenshinInfo {
    @Id
    var id: String? = null
    var qqNumber: String = ""
    var nickName: String = "未知用户"
    var cookie: String = ""
    var uid: String = "100"
        set(value) {
            if (uid[0] >= '5'){
                this.serverType = ServerType.FOREIGN
            }
            field = value
        }

    @Transient
    var serverType: ServerType = ServerType.OFFICIAL

    constructor()

    constructor( uid: String ){
        this.uid = uid
    }

    constructor( uid: String, nickName: String, cookie: String ): this(uid){
        this.nickName = nickName
        this.cookie = cookie

    }
}