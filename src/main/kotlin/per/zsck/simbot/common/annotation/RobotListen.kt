package per.zsck.simbot.common.annotation

import love.forte.simboot.annotation.ContentTrim
import love.forte.simboot.annotation.Listener
import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.GroupMessageEvent
import org.springframework.core.annotation.AliasFor
import per.zsck.simbot.core.permit.Permit
import per.zsck.simbot.core.state.enums.GroupStateEnum
import kotlin.reflect.KClass

/**
 * @author wuyou 、 zsck
 */
@Suppress("OPT_IN_USAGE", "unused")
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Listener
@ContentTrim
annotation class RobotListen(
    /**
     * 描述信息
     */
    val desc: String = "",
    /**
     * 执行监听器所需的权限
     */
    val permission: Permit = Permit.MEMBER,
    /**
     * 没有权限时的提示信息
     */
    val noPermissionTip: String = "操作失败,您没有权限",
    /**
     * 此事件的优先级。
     */
    @get:AliasFor(attribute = "priority", annotation = Listener::class) val priority: Int = PriorityConstant.NORMAL,

    @get:AliasFor(attribute = "id", annotation = Listener::class) val id: String = "",

    /**
     * 是否在当前群开机的时候执行,仅当监听类型是[GroupMessageEvent]时有效
     */
    val stateLeast: GroupStateEnum = GroupStateEnum.NORMAL,

    /**
     * 额外的事件功能开关判断
     */
    vararg val boolEnumCondition: KClass<*>,

    val memberMessageSupport: Boolean = true,

)