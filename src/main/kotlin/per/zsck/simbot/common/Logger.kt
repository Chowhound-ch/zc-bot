package per.zsck.simbot.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author zsck
 * @date   2022/10/31 - 18:46
 */

fun  Any.logInfo(msg : String, vararg params: Any) {
    Log.log(this.javaClass, Log.LogLevel.INFO, msg, params)
}
fun  Any.logTrace(msg : String, vararg params: Any) {
    Log.log(this.javaClass, Log.LogLevel.TRACE, msg, params)
}
fun  Any.logError(msg : String, vararg params: Any) {
    Log.log(this.javaClass, Log.LogLevel.ERROR, msg, params)
}
fun  Any.logDebug(msg : String, vararg params: Any) {
    Log.log(this.javaClass, Log.LogLevel.DEBUG, msg, params)
}
fun  Any.logWarn(msg : String, vararg params: Any) {
    Log.log(this.javaClass, Log.LogLevel.WARN, msg, params)
}

object Log{
    private val logs : MutableMap<Class<Any>, Logger> = HashMap()

    fun log(clazz: Class<Any>, level: LogLevel, msg: String, params: Array<out Any>){

        val logger = logs.computeIfAbsent(clazz) {
            LoggerFactory.getLogger(it)
        }
        when (level){
            LogLevel.INFO -> logger.info(msg, *params)
            LogLevel.TRACE -> logger.trace(msg, *params)
            LogLevel.DEBUG -> logger.debug(msg, *params)
            LogLevel.WARN -> logger.warn(msg, *params)
            LogLevel.ERROR -> logger.error(msg, *params)
        }
    }

    enum class LogLevel{
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }
}
