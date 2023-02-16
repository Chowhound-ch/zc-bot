package per.zsck.simbot.http.image

import cn.hutool.core.util.URLUtil
import per.zsck.custom.util.http.HttpUtil

/**
 * @author zsck
 * @date   2023/2/15 - 15:12
 */

object PixivApi{

    private const val PIXIV_API = "https://api.lolicon.app/setu/v2"

    fun getImage(tag: List<String>, r18: Boolean){
        val url = getUrl(tag, r18)

        val res = HttpUtil.doGetJson(url)

        res["data"]?.apply {

        }

    }


    private fun getUrl(tag: List<String>, r18: Boolean): String{
        var query = "?r18=${if (r18) { 1 }else{ 0 }}"
        tag.forEach { query += "&tag=${URLUtil.encode(it)}" }


        return PIXIV_API + query
    }

}

fun main(){
    PixivApi.getImage(arrayListOf("刻晴"), false)
}