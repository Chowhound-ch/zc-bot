package per.zsck.simbot.http.kugou.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author zsck
 * @date   2022/11/10 - 12:37
 */
@TableName("music")
data class Music(
    @TableId
    var id: Int? = null,

    @field:JsonAlias("FileName")
    var audioName: String? = null,

    @field:JsonAlias("SongName", "song_name")
    var title: String? = null,

    @field:JsonAlias("author_name")
    var artist: String? = null,
    var md5: String? = null,

    @field:JsonAlias("play_backup_url")
    var url: String? = null,

    @TableField(exist = false)
    @field:JsonAlias("img")
    var imgUrl: String? = null,

    @field:JsonAlias("FileHash")
    @TableField(exist = false)
    var fileHash: String? = null,

    @TableField(exist = false)
    @field:JsonAlias("AlbumID")
    var albumID: String? = null

){

}