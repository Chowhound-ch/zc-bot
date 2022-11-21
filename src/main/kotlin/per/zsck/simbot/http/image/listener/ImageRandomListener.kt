package per.zsck.simbot.http.image.listener

import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.FilterValue
import love.forte.simbot.action.sendIfSupport
import love.forte.simbot.component.mirai.message.buildMiraiForwardMessage
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.GroupMessageEvent
import love.forte.simbot.message.buildMessages
import love.forte.simbot.resources.ByteArrayResource
import love.forte.simbot.resources.URLResource

import org.springframework.stereotype.Controller
import per.zsck.simbot.common.annotation.RobotListen
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

    @RobotListen
    @Filter("/图片{{number,([1-9][0-9]?)?}}")
    suspend fun GroupMessageEvent.getRandomImage(
        @FilterValue("number")number: String
    ): EventResult{
        val desNum = if (number == "") 1 else number.toInt()

        if (desNum > 50){
            sendIfSupport("数量太多啦,请调整数量后再尝试")
            return EventResult.truncate()
        }else{
            if ( desNum > 10 ){
                sendIfSupport("单次请求的图片较多，请耐心等待")
            }

            val imageUrlList = imageRandom.getImageUrlList(desNum)

            val author = author()

            sendIfSupport(buildMiraiForwardMessage {
                imageUrlList.forEach {

                    this.add(author.id, author.nickname,   buildMessages { this.image( URLResource(URL(it.asText()))) } )
                }
            })
        }

        return EventResult.truncate()
    }

    @RobotListen
    @Filter("/随机cos")
    suspend fun GroupMessageEvent.randomCos(){

        sendIfSupport( buildMessages {
            this.image( ByteArrayResource("randomCos", imageRandom.getRandomCos()) )
        } )
    }

    @RobotListen
    @Filter("/买家秀")
    suspend fun GroupMessageEvent.buyersShow(){
        sendIfSupport(
            buildMessages {
                imageRandom.getBuyersShow().let {
                    this.append(it["title"].asText())
                    this.image(ByteArrayResource("buyersShow", imageRandom.getImageBuyUrl( it["pic"].asText() )))
                }
            }
        )
    }
}