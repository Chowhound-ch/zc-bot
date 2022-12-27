package per.zsck.music.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import per.zsck.music.utils.MusicUtil;

/**
 * @author zsck
 * @date 2022/12/11 - 11:15
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document("music")
public class Music {

    @Id
    String id;
    String audioName;
    /**
     * 根据audioName设置
     */
    String audioNameIndex;
    String title;
    String artist;
    String md5;
    /**
     * 根据fileId设置
     */
    String url;
    String fileName;

    ObjectId fileId;

    public Music(String audioName, String md5, ObjectId fileId) {
        this.setAudioName( audioName );
        this.md5 = md5;
        this.setFileId(fileId );
    }

    public void setFileId(ObjectId fileId) {
        this.fileId = fileId;
        this.url = MusicUtil.URL_PREFIX + fileId.toString();
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
        this.audioNameIndex = audioName.replaceAll("\\s", "");
    }
}
