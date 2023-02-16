package per.zsck.simbot.http.image.listener

import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.FilterValue
import love.forte.simbot.component.mirai.message.buildMiraiForwardMessage
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.GroupMessageEvent
import love.forte.simbot.message.buildMessages
import love.forte.simbot.resources.URLResource
import org.springframework.stereotype.Controller
import per.zsck.simbot.common.annotation.RobotListen
import per.zsck.simbot.core.state.enums.ImageEnum
import per.zsck.simbot.http.image.ImageRandom
import java.net.URL

/**
 * @author zsck
 * @date   2022/11/17 - 15:21
 */
@Controller
class ImageRandomListener(
    val imageRandom: ImageRandom
) {

    @RobotListen(boolEnumCondition = [ImageEnum::class])
    @Filter("/来点好(看|康)的{{number,([1-9][0-9]?)?}}")
    suspend fun GroupMessageEvent.getRandomImage(
        @FilterValue("number")number: String
    ): EventResult{
        val desNum = if (number == "") 1 else number.toInt()
        val group = this.group()
        if (desNum > 50){
            group.send("数量太多啦,请调整数量后再尝试")
            return EventResult.truncate()
        }else{
            if ( desNum > 10 ){
                group.send("单次请求的图片较多，请耐心等待")
            }

            val imageUrlList = imageRandom.getImageUrlList(desNum)

            val author = author()

            group.send(buildMiraiForwardMessage {
                imageUrlList.forEach {

                    this.add(author.id, author.nickname,   buildMessages { this.image( URLResource(URL(it.asText()))) } )
                }
            })
        }

        return EventResult.truncate()
    }
}