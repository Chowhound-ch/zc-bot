package per.zsck.simbot.http.image

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import org.apache.http.Header
import org.apache.http.HttpHeaders
import org.apache.http.message.BasicHeader
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import per.zsck.simbot.common.utils.HttpBase

/**
 * @author zsck
 * @date   2022/11/17 - 15:10
 */
@Component
class ImageRandom : HttpBase(){


    companion object{

        private const val IMAGE_API_URL = "http://api.iw233.cn/api.php?sort=random&type=json&num="

        private const val RANDOM_COS_URL = "https://api.vvhan.com/api/girl?type=json"

        private const val BUYERS_SHOW_URL = "https://api.vvhan.com/api/tao?type=json"



        const val USER_AGENT = "apifox/1.0.0 (https://www.apifox.cn)"
        const val ACCEPT = "*/*"
        const val HOST = "api.vvhan.com"
        const val ACCEPT_ENCODING = "gzip, deflate, br"

        fun getImagesRandomUrl(number: Int): String{
            return IMAGE_API_URL + number
        }

    }

    fun getImageUrlList(number: Int): ArrayNode{
        return doGetJson( getImagesRandomUrl(number) )["pic"] as ArrayNode
    }


    fun getRandomCos(): ByteArray{

        return getImageBuyUrl(
            doGetJson(RANDOM_COS_URL)["imgurl"].asText()
        )
    }

    fun getBuyersShow(): JsonNode{
        return doGetJson(BUYERS_SHOW_URL)
    }

    fun getImageBuyUrl(url: String): ByteArray{
        return doGetBytes(url)
    }


    override fun getHeader(): Array<Header>? {
        return arrayOf(
            BasicHeader(HttpHeaders.USER_AGENT, USER_AGENT),
            BasicHeader(HttpHeaders.ACCEPT, ACCEPT),
            BasicHeader(HttpHeaders.ACCEPT_ENCODING, ACCEPT_ENCODING),
        )
    }
}


