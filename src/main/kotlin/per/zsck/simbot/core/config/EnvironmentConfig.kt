package per.zsck.simbot.core.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

/**
 * @author zsck
 * @date   2023/2/14 - 16:17
 */
@Configuration
class EnvironmentConfig {
    companion object{
        lateinit var environment: String

        const val DEV = "dev"

        const val PROD = "prod"


        fun isDev(): Boolean{
            return environment == DEV
        }

    }

    @Value("\${zsck.config.environment}")
    fun setEnvironment(environment: String){
        EnvironmentConfig.environment = environment
    }


}