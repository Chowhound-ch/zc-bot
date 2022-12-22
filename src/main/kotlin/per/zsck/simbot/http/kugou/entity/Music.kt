package per.zsck.simbot.http.kugou.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import org.springframework.data.annotation.Transient
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author zsck
 * @date   2022/11/10 - 12:37
 */
@Document("music")
class Music{

    @Id
    var id: String? = null

    @JsonAlias("FileName")
    var audioName: String? = null
        set(value) {
            audioNameIndex = value?.replace("\\s", "")
            field = value
        }

    /**
     * 不包含空格的audioName，用于模糊查询
     */
    var audioNameIndex: String? = null

    @JsonAlias("SongName", "song_name")
    var title: String? = null

    @JsonAlias("author_name")
    var artist: String? = null
    var md5: String? = null

    @JsonAlias("play_backup_url")
    var url: String? = null

    @Transient
    @JsonAlias("img")
    var imgUrl: String? = null

    @JsonAlias("FileHash")
    @Transient
    var fileHash: String? = null

    @Transient
    @JsonAlias("AlbumID")
    var albumID: String? = null

    var file: ByteArray? = null
}