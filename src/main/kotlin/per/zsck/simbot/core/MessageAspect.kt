package per.zsck.simbot.core

import kotlinx.coroutines.runBlocking
import love.forte.simbot.ID
import love.forte.simbot.component.mirai.event.MiraiMemberMessageEvent
import love.forte.simbot.definition.Group
import love.forte.simbot.event.Event
import love.forte.simbot.event.GroupMessageEvent
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import per.zsck.simbot.common.annotation.RobotListen
import per.zsck.simbot.common.logInfo
import per.zsck.simbot.core.config.MiraiBotManagerSupport
import per.zsck.simbot.core.permit.Permit
import per.zsck.simbot.core.permit.service.PermitDetailService
import per.zsck.simbot.core.state.GroupStateCache
import per.zsck.simbot.core.state.enums.EnumUtil
import per.zsck.simbot.core.state.enums.GroupStateEnum
import per.zsck.simbot.core.state.service.GroupStateService

/**
 * @author zsck
 * @date   2022/11/5 - 13:53
 */
@Aspect
@Component
class MessageAspect(
    val permitDetailService: PermitDetailService,
    val groupStateService: GroupStateService
    ): MiraiBotManagerSupport() {



    // 消息监听，拦截所有带有@RobotListen注解的方法
    @Around("@annotation(per.zsck.simbot.common.annotation.RobotListen) && @annotation(annotation))")
    fun ProceedingJoinPoint.doAroundAdvice(annotation: RobotListen): Any? {
        val event = args.find { it is Event } ?: return proceed()
        val start = System.currentTimeMillis()

        fun proceedSuccess(group: String = "好友消息"): Any? {
            logInfo("执行了监听器{}({})(群: {}), 拦截器耗时: {}"
                ,signature.name, annotation.desc, group, System.currentTimeMillis() - start)

            return proceed()
        }

        fun proceedFailed(tip: String? = null, group: String) {
            logInfo("执行监听器{}({})(群: {}) 失败 : {}", signature.name, annotation.desc, group, tip ?: "无")

            runBlocking { tip?.let {
                miraiBot.group(group.ID)?.send(it)
            } }

        }
        fun hasNoPermission(number: String, permission: Permit): Boolean {
            return (permission.value != Permit.MEMBER.value) && (permission.value >  permitDetailService.getPermit( number ).permit.value)
        }


        fun checkGroupState(number: String, group: Group): Any? {

            // 判断是否有权限
            if ( hasNoPermission(number, annotation.permission) ) {

                return proceedFailed(annotation.noPermissionTip, group.id.toString())

            }

            val groupState = groupStateService.getGroupState(group.id.toString())
            // 判断是否开机
            if (groupState.state!! < annotation.stateLeast){

                return proceedFailed("当前群未开放此功能", group.id.toString()) //未开机则拦截

            }else if (groupState.state != GroupStateEnum.OPENED_ALL){ // 正常开机则进一步判断RobotListen.boolEnumCondition中的条件

                annotation.boolEnumCondition.forEach {

                    val field = GroupStateCache.getByClass(it.java) // 通过反射获取枚举类的字段


                    if (field.get(groupState) == EnumUtil.getInstance(it.java, 0)){// 如果字段值为value为0的枚举则拦截
                        return proceedFailed("当前群未开放此功能: ${field.type.simpleName}", group.id.toString())
                    }
                }

            }
            // 完全开机则直接放行

            return proceedSuccess(group.id.toString())
        }





        // 判断是否为群消息
        if (event is GroupMessageEvent) {
            val group = runBlocking { event.group() }
            val author = runBlocking { event.author() }.id.toString()




            return checkGroupState(author, group)
        }

        // 临时会话
        if (event is MiraiMemberMessageEvent) {

            val member = runBlocking { event.user() }
            val group = runBlocking { member.group() }

            if (!annotation.memberMessageSupport){
                return proceedFailed("不支持临时会话", group.id.toString())
            }


            return checkGroupState(member.id.toString(), group)
       }

        return proceedSuccess()
    }
}