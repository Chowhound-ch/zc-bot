package per.zsck.simbot

import love.forte.simboot.spring.autoconfigure.EnableSimbot
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import kotlin.math.log

@EnableAspectJAutoProxy
@EnableSimbot
@SpringBootApplication
class ZcBotApplication

fun main(args: Array<String>) {
    runApplication<ZcBotApplication>(*args)

}
