package per.zsck.simbot.core.permit.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import per.zsck.simbot.common.logInfo
import per.zsck.simbot.common.logWarn
import per.zsck.simbot.core.permit.Permit
import per.zsck.simbot.core.permit.service.PermitDetailService
import java.util.Objects
import javax.annotation.PostConstruct

/**
 * @author zsck
 * @date   2022/11/6 - 14:14
 */
@ConditionalOnProperty(prefix = "zsck.permit", value = ["is-init"], havingValue = "true")
@Configuration
class PermitInitConfig(
    val permitDetailService: PermitDetailService
) {
    @Value("\${zsck.permit.owner}")
    var owner: String? = null

    @PostConstruct
    fun init(){
        if (owner != null){
            logInfo("已开启自动维护bot所有者权限")
            permitDetailService.setUsedHostPermit(owner!!, Permit.MEMBER)
            if (permitDetailService.setPermit(owner!!, Permit.HOST)) {
                logInfo("自动修正bot所有者权限")
            }else{
                logInfo("bot所有者权限正常")
            }
        }else{
            logWarn("设置自动维护bot所有者权限但并未配置zsck.permit.owner")
        }

    }
}