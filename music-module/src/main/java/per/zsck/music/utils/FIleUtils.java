package per.zsck.music.utils;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.temporal.TemporalField;

/**
 * @author zsck
 * @date 2022/12/13 - 12:07
 */
@Slf4j
public class FIleUtils {

    public static String getFileNamePrefix(String fileName){
        String[] split = fileName.split("\\.");
        String fileEnd = split[split.length - 1];//文件后缀 如 mp3 , zip
        return fileName.substring(0, fileName.lastIndexOf(fileEnd) - 1);
    }

    public static String getFileNameSuffix(String fileName){
        String[] split = fileName.split("\\.");
        return split[split.length - 1];//文件后缀 如 mp3 , zip
    }

    public static File createTempFileAndLog(String suffix){
        File tempFile = FileUtil.createTempFile(suffix, true);
        log.info( "创建临时文件: {}", tempFile.getName() );

        return tempFile;
    }

    public static File createTempFileAndLog(){
        File tempFile = FileUtil.createTempFile();
        log.info( "创建临时文件: {}", tempFile.getName() );
        return tempFile;
    }

    public static boolean deleteAndLog(File file){
        boolean delete = file.delete();
        if (delete) {
            log.info("文件: {} 删除成功", file.getAbsolutePath() );
        }else {
            log.warn("文件: {} 删除失败", file.getAbsolutePath() );

        }

        return delete;
    }
}
