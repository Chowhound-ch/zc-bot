package per.zsck.simbot.http.image

import per.zsck.custom.util.http.HttpBase
import com.fasterxml.jackson.databind.node.ArrayNode
import org.springframework.stereotype.Component

/**
 * @author zsck
 * @date   2022/11/17 - 15:10
 */
@Component
class ImageRandom : HttpBase(){


    companion object{

        private const val IMAGE_API_URL = "http://api.iw233.cn/api.php?sort=random&type=json&num="


        fun getImagesRandomUrl(number: Int): String{
            return IMAGE_API_URL + number
        }

    }

    fun getImageUrlList(number: Int): ArrayNode{
        return doGetJson( getImagesRandomUrl(number) )["pic"] as ArrayNode
    }



    fun getImageBuyUrl(url: String): ByteArray{
        return doGetBytes(url)
    }



}


