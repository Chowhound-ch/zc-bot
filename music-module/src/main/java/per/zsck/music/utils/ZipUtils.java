package per.zsck.music.utils;

import cn.hutool.core.io.IoUtil;
import org.springframework.stereotype.Component;
import per.zsck.music.entity.Music;
import per.zsck.music.service.MusicService;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author zsck
 * @date 2022/12/13 - 10:49
 */
@Component
public class ZipUtils {
    @Resource
    private MusicService musicService;

    public static final String ZIP_END = "zip";

    public static boolean isZipFile(String fileSuffix){
        return fileSuffix.equals( ZIP_END );
    }

    /**
     * 读取zip文件中所有音频文件并保存到库中，返回所有音频文件信息
     * @param file
     * @return
     */
    public List<Music> uploadMusicZipFile(File file){
        try (ZipFile zipFile = new ZipFile(file, Charset.forName("GBK"))){
            List<Music> resList = new ArrayList<>();

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while ( entries.hasMoreElements() ){
                ZipEntry entry = entries.nextElement();

                try {
                    if (! entry.isDirectory() && MusicUtil.isMusicSuffix( FIleUtils.getFileNameSuffix(entry.getName()) )) {
                        //是音频文件则将其保存到库
                        resList.add(
                                musicService.analysisAndUpload(
                                        entry.getName().substring( entry.getName().lastIndexOf("/") + 1 ),
                                        IoUtil.readBytes(zipFile.getInputStream(entry))
                                )
                        );

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return resList;
        } catch (IOException e) {
            e.printStackTrace();
        }
       return null;
    }

}
