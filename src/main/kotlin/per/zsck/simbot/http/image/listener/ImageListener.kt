package per.zsck.simbot.http.image.listener

import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.FilterValue
import love.forte.simbot.component.mirai.message.buildMiraiForwardMessage
import love.forte.simbot.definition.Group
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.GroupMessageEvent
import love.forte.simbot.event.MessageEvent
import love.forte.simbot.message.Messages
import love.forte.simbot.message.buildMessages
import love.forte.simbot.resources.ByteArrayResource
import love.forte.simbot.resources.URLResource
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.stereotype.Controller
import per.zsck.simbot.common.annotation.RobotListen
import per.zsck.simbot.common.logError
import per.zsck.simbot.core.state.enums.ImageEnum
import per.zsck.simbot.core.state.enums.ImageR18Enum
import per.zsck.simbot.http.HttpProxy
import per.zsck.simbot.http.image.ImageRandom
import per.zsck.simbot.http.image.api.PixivApi
import per.zsck.simbot.http.image.entity.ImageEntity
import java.net.URL

/**
 * @author zsck
 * @date   2022/11/17 - 15:21
 */
@Controller
class ImageListener(
    val imageRandom: ImageRandom
) {

    @RobotListen(boolEnumCondition = [ImageEnum::class], memberMessageSupport = false)
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
    @RobotListen(boolEnumCondition = [ImageEnum::class, ImageR18Enum::class], memberMessageSupport = false)
    @Filter("/来点{{tags}}(r18|R18)")
    suspend fun MessageEvent.someImageWithTagsR18(
        @FilterValue("tags")tags: String
    ): EventResult{

        val imageEntity = PixivApi.getImage(tags.split("\\s+".toRegex()), true) ?: let {
            this.reply("没有找到相关图片")
            return EventResult.truncate()
        }


        return sendImageMsg(imageEntity, this)
    }

    @RobotListen(boolEnumCondition = [ImageEnum::class])
    @Filter("/来点{{tags}}涩图")
    suspend fun MessageEvent.someImageWithTags(
        @FilterValue("tags")tags: String
    ): EventResult{
        val imageEntity = PixivApi.getImage(tags.split("\\s+".toRegex()), false) ?: let {
            this.reply("没有找到相关图片")
            return EventResult.truncate()
        }


        return sendImageMsg(imageEntity, this)
    }


    suspend fun sendImageMsg(imageEntity: ImageEntity, event: MessageEvent): EventResult{

        try {
            event.reply(getImageMsg(imageEntity)).let { res->
                if (!res.isSuccess){
                    event.reply("发送失败,未知错误")
                }
            }
        }catch (e: IllegalArgumentException){
            logError("发送涩图失败:{}, errMsg: {}", imageEntity.urls.original, e.message ?: "无")
            event.reply("发送失败: url:${imageEntity.urls.original}")
        }



        return EventResult.truncate()
    }





    fun getImageMsg(imageEntity: ImageEntity): Messages{
       return buildMessages{
//                    this.image(it.urls.original.toURLResource())
           this.image(ByteArrayResource("image", HttpProxy.doGetBytes(imageEntity.urls.original)))
           this.text("title:${imageEntity.title}\n")
           this.text("r18:${imageEntity.r18}\n")
           this.text("tags:${imageEntity.tags}\n")
           this.text("url:${imageEntity.urls.original}")
        }
    }
}