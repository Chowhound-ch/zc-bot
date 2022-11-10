package per.zsck.simbot.common.enums

import love.forte.simbot.definition.Role

/**
 * @author zsck
 * @date   2022/10/31 - 9:42
 */
enum class RobotPermission (private val level: Int) {
    MEMBER(0b1), ADMINISTRATOR(0b10), OWNER(0b100);

    operator fun compareTo(robotPermission: Int): Int {
        return if (this.level > 1) 1 else -1
    }
}