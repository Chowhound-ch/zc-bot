package per.zsck.simbot.core.state

import com.baomidou.mybatisplus.annotation.EnumValue
import kotlinx.coroutines.runBlocking

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
    NORMAL(1, "正常开机"),

    /**
     * 额外开启米游社签到推送
     */
    OPENED_ALL(2, "完全开机");


    companion object{

        private val map = HashMap<Int, GroupStateEnum>().apply {
            values().forEach {
                this[it.value] = it
            }
        }

        fun getInstance(value: Int): GroupStateEnum = map[value]!!
    }

    override fun toString(): String {
        return des
    }
}