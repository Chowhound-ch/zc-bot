package per.zsck.simbot.core.permit

import com.baomidou.mybatisplus.annotation.EnumValue

/**
 * @author zsck
 * @date   2022/11/5 - 13:42
 */
enum class Permit(
    @EnumValue
    val value: Int
) {
    HOST(3),
    MANAGER(2),
    MEMBER(1);
}
