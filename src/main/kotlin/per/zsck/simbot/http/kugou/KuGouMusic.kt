package per.zsck.simbot.http.kugou

import per.zsck.custom.util.http.HttpBase
import cn.hutool.core.net.URLEncodeUtil
import cn.hutool.core.util.ReUtil
import org.springframework.stereotype.Component
import per.zsck.custom.util.jackson.JacksonUtil
import per.zsck.simbot.common.logWarn
import per.zsck.simbot.http.kugou.entity.Music
import per.zsck.simbot.http.kugou.entity.MusicRes
import java.io.IOException
import kotlin.streams.toList

/**
 * @author zsck
 * @date   2022/11/10 - 12:28
 */
@Component
class KuGouMusic: HttpBase() {


    fun getMusicUrlByAlbumIDAndHash(music: Music): Music? {
        return getMusicDetail(music.albumID, music.fileHash)?.music
    }

    fun getOneMusicRes(keyWord: String): Music?{
        return try {
            getMusicUrlByAlbumIDAndHash(getSearchRes(keyWord, 1)[0])
        }catch (e: IndexOutOfBoundsException){ null }
    }


    /**
     * 返回多个带AlbumID和FileHash的music对象，不可直接使用
     */
    fun getSearchRes(keyWord: String?, number: Int = 5): MutableList<Music> {
        try {
            val searchResStr = ReUtil.get("\\((.+)\\)", doGetStr(encoding(keyWord!!)), 1)
            val searchResJson = JacksonUtil.readTree(searchResStr)

            //默认取搜索结果前number歌，searchResJsonObj.optJSONObject("data").optJSONArray("lists") 为所有搜索结果
            val list  = JacksonUtil.readValue(searchResJson["data"]["lists"].toString(), Array<Music>::class.java)

            return list.toList().stream().limit(number.toLong()).peek {
                it.audioName = it.audioName?.replace(Regex("</?em>"), "")
            }.toList() as MutableList<Music>
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ArrayList()
    }

    private fun getMusicDetail(albumID: String?, hash: String?): MusicRes? {
        val urlForMusicDetail = "https://wwwapi.kugou.com/yy/index.php?r=play/getdata&callback=jQuery191033144701096575724_1660124702942&hash=$hash&dfid=3eyKKr1tAQle0EQs9n1ItnQV&appid=1014&mid=d30a3efc49071a50132e4b338f93aa0a&platid=4&album_id=$albumID&_=1660124702944"
        try {
            val detailRes = ReUtil.get("\\((.+)\\)", doGetStr(urlForMusicDetail), 1)
            val musicRes: MusicRes = JacksonUtil.readValue(detailRes, MusicRes::class.java)

            musicRes.music ?: logWarn("获取歌曲url失败，错误的返回信息: {}", detailRes)
            return musicRes
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    private fun encoding(keyWord: String): String = "https://complexsearch.kugou.com/v2/search/song?callback=callback123&keyword=${URLEncodeUtil.encode(keyWord)}&page=1&pagesize=30&bitrate=0&isfuzzy=0&tag=em&inputtype=0&platform=WebFilter&userid=-1&clientver=2000&iscorrection=1&privilege_filter=0&srcappid=2919&clienttime=1600305065609&mid=1600305065609&uuid=1600305065609&dfid=-&signature=${MD5Encoding.MD5(keyWord)}"

}