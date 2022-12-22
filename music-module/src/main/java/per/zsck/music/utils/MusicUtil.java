package per.zsck.music.utils;

import cn.hutool.core.util.ReUtil;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import per.zsck.music.entity.Music;

import java.util.List;

/**
 * @author zsck
 * @date 2022/12/13 - 9:15
 */
@Configuration
public class MusicUtil {
    public static String URL_PREFIX;
    public static final List<String> musicEnds = List.of( "mp3", "flac", "ape", "wav" );

    @Value("${per.zsck.music.url-prefix}")
    public void setUrlPrefix(String urlPrefix) {
        MusicUtil.URL_PREFIX = urlPrefix;
    }

    /**
     * 根据 audioFile 分析title和artist
     */
    public static Music analysisOfAudioFile(AudioFile audioFile, Music music){
        String title = audioFile.getTag().getFirst(FieldKey.TITLE);
        if (ReUtil.isMatch("\\w+", title)){
            music.setTitle(title);
        }
        String artist = audioFile.getTag().getFirst(FieldKey.ARTIST);
        if (ReUtil.isMatch("\\w+", artist)){
            music.setArtist(artist);
        }

        music.setAudioName(music.getArtist() + " - " + music.getTitle());
        return music;
    }

    public static boolean isMusicSuffix(String fileSuffix){
        for (String musicEnd : musicEnds) {
            if (musicEnd.equals( fileSuffix )){
                return true;
            }
        }
        return false;
    }
}
