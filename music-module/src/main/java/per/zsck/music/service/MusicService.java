package per.zsck.music.service;


import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import per.zsck.music.entity.Music;

import java.util.List;

/**
 * @author zsck
 * @date 2022/12/11 - 11:35
 */

public interface MusicService {

    boolean removeMusicByFileId(String fileId);

    GridFsResource getMusicFileByFileId(String fileId);

    List<Music> likeMusic(String keyword);

    ObjectId uploadMusicAndReplace(Music music, byte[] bytes);

    Music analysisAndUpload(String fileName, byte[] bytes);

}
