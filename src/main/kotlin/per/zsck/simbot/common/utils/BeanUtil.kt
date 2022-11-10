package per.zsck.simbot.common.utils

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.springframework.context.annotation.Lazy

/**
 * @author zsck
 * @date   2022/11/6 - 12:33
 */
@Component
class BeanUtil{
    companion object{
        lateinit var applicationContext: ApplicationContext

        fun <T> getBean(clazz: Class<T>): T{
            return applicationContext.getBean(clazz)
        }
    }

    @Autowired
    fun getApplication(applicationContext: ApplicationContext){
        BeanUtil.applicationContext = applicationContext
    }

}