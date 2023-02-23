package per.zsck.simbot.core.state.enums

import cn.hutool.core.util.ClassUtil
import com.baomidou.mybatisplus.annotation.EnumValue
import per.zsck.simbot.core.state.annotation.ValueEnumCache

/**
 * @author zsck
 * @date   2022/11/5 - 13:34
 */
@Suppress("unused")
object EnumUtil{

    val BOOL_ENUM_MAP: MutableMap<Class<*>, String> = HashMap()

    val enumCache = ClassUtil.scanPackageByAnnotation("per.zsck.simbot.core.state.enums", ValueEnumCache::class.java)
            .filter { it.isEnum }
            .onEach {
                if (it.enumConstants.size == 2) {
                    BOOL_ENUM_MAP[it] = it.typeName
                }
            } // 记录boolEnum
            .associateWith { clazz ->
                    clazz.enumConstants.associateBy { enum ->
                        enum.javaClass.declaredFields.first { field ->
                            field.isAnnotationPresent(EnumValue::class.java)
                        }.apply { isAccessible = true }.get(enum)// 获取@EnumValue注解的值
                    }
                }


    inline fun <reified T : Enum<*>> getInstance(enumClass: Class<*>, value: Any): T? = enumCache[enumClass]?.get(value) as T?

    inline fun <reified T : Enum<*>> getInstance(value: Any): T? = enumCache[T::class.java]?.get(value) as T?

//    fun getSimpleInstance(enumClass: Class<*>, value: Any): Enum<*>? = enumCache[enumClass]?.get(value)?.let { it as Enum<*>}

}

@ValueEnumCache
enum class GroupStateEnum(
    @EnumValue
    val value: Int,
    private val des: String,
){
    CLOSED(0, "关机"),
    NORMAL(1, "正常开机"),


    OPENED_ALL(2, "完全开机");

    override fun toString(): String {
        return des
    }

}
@ValueEnumCache
enum class LessonPushEnum(
    @EnumValue
    val value: Int,
    private val des: String,
){
    CLOSED(0, "关闭课表推送"),
    NORMAL(1, "开启课表推送");

    override fun toString(): String {
        return des
    }
}
@ValueEnumCache
enum class GenshinSignPushEnum(
    @EnumValue
    val value: Int,
    private val des: String,
){
    CLOSED(0, "关闭签到推送"),
    NORMAL(1, "开启签到推送");

    override fun toString(): String {
        return des
    }
}
@ValueEnumCache
enum class CanHeartEnum(
    @EnumValue
    val value: Int,
    private val des: String,
){
    CLOSED(0, "关闭"),
    NORMAL(1, "开启");

    override fun toString(): String {
        return des
    }
}
