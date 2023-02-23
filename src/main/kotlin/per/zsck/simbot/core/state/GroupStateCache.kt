package per.zsck.simbot.core.state

import cn.hutool.core.util.ClassUtil
import per.zsck.simbot.core.state.entity.GroupState
import per.zsck.simbot.core.state.enums.EnumUtil
import java.lang.reflect.Field

/**
 * @author zsck
 * @date   2023/2/13 - 18:11
 */
object GroupStateCache {
    val ENUM_MAP: MutableMap<Class<*>, Field> = HashMap()

    init {
        ClassUtil.getDeclaredFields(GroupState::class.java).forEach {
            it.isAccessible = true
            if (it.type.isEnum) {
                ENUM_MAP[it.type] = it
            }
        }
    }

    fun getClassByFieldName(name: String): Class<*>? = ENUM_MAP.firstNotNullOf { (clazz, field) ->
        if (field.name.uppercase() == name.uppercase()) clazz else null
    }


    fun getByClass(clazz: Class<*>): Field = ENUM_MAP[clazz]!!
}