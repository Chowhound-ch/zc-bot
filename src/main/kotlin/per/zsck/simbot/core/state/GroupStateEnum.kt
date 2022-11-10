package per.zsck.simbot.core.state

import com.baomidou.mybatisplus.annotation.EnumValue

/**
 * @author zsck
 * @date   2022/11/5 - 13:34
 */
enum class GroupStateEnum(
    @EnumValue
    val value: Int,
    val des: String,
) {
    CLOSED(0, "关机"),
    OPENED(1, "开机");

    override fun toString(): String {
        return des
    }}