package per.zsck.simbot.http.image.api

import cn.hutool.core.util.URLUtil
import per.zsck.custom.util.jackson.JacksonUtil
import per.zsck.simbot.http.HttpProxy
import per.zsck.simbot.http.image.entity.ImageEntity

/**
 * @author zsck
 * @date   2023/2/15 - 15:12
 */

object PixivApi{

    private const val PIXIV_API = "https://api.lolicon.app/setu/v2"

    fun getImage(tag: List<String>, r18: Boolean): ImageEntity?{
        val url = getUrl(tag, r18)

        val res = HttpProxy.doGetJson(url)

         return res["data"]?.let {
            val listValue = JacksonUtil.readListValue(JacksonUtil.toJsonString(it), ImageEntity::class.java)
             if (listValue.isEmpty()) null else listValue[0]
        }

    }


    private fun getUrl(tag: List<String>, r18: Boolean): String{
        var query = "?r18=${if (r18) { 1 }else{ 0 }}"
        tag.forEach { query += "&tag=${URLUtil.encode(it)}" }


        return PIXIV_API + query
    }

}

