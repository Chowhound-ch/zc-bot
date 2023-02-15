package per.zsck.simbot.core.state.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import per.zsck.simbot.core.state.enums.CanHeartEnum
import per.zsck.simbot.core.state.enums.GenshinSignPushEnum
import per.zsck.simbot.core.state.enums.GroupStateEnum
import per.zsck.simbot.core.state.enums.LessonPushEnum
import java.io.Serializable

/**
 * @author zsck
 * @date   2022/11/5 - 11:14
 */
@Document("group_state")
data class GroupState(
    @Id
    var id: String? = null,
    var groupNumber: String? = null,
    var state: GroupStateEnum? = GroupStateEnum.CLOSED,

    var lessonPush: LessonPushEnum? = LessonPushEnum.CLOSED,
    var genshinSignPush: GenshinSignPushEnum? = GenshinSignPushEnum.CLOSED,
    var canHeart: CanHeartEnum? = CanHeartEnum.CLOSED,
) :Serializable{

//    constructor(groupNumber: String): this(null, groupNumber, null, null, null, null)

    companion object {
        fun of(groupNumber: String): GroupState {
            return GroupState(null, groupNumber, null, null, null, null)
        }

        fun instance(): GroupState {
            return GroupState(null, null, null, null, null, null)
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GroupState

        if (groupNumber != other.groupNumber) return false
        if (state != other.state) return false
        if (lessonPush != other.lessonPush) return false
        if (genshinSignPush != other.genshinSignPush) return false
        if (canHeart != other.canHeart) return false

        return true
    }

    override fun hashCode(): Int {
        var result = groupNumber?.hashCode() ?: 0
        result = 31 * result + state.hashCode()
        result = 31 * result + lessonPush.hashCode()
        result = 31 * result + genshinSignPush.hashCode()
        result = 31 * result + canHeart.hashCode()
        return result
    }
}