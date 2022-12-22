package per.zsck.music.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import per.zsck.music.entity.Music;
import per.zsck.music.service.MusicService;
import per.zsck.music.utils.FIleUtils;
import per.zsck.music.utils.MusicUtil;
import per.zsck.music.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zsck
 * @date 2022/12/12 - 15:35
 */
@RequestMapping("/bot/music")
@RestController
@Slf4j
public class MusicController {

    @Autowired
    private MusicService musicService;
    @Autowired
    private ZipUtils zipUtils;

    @PostMapping("/")
    public List<String>  uploadMusic(MultipartFile file){

        List<String> desMusicInfos = new ArrayList<>();


        String fileName = file.getOriginalFilename();

        byte[] bytes = new byte[0];
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull( fileName );

        String fileEnd = FIleUtils.getFileNameSuffix( fileName );

        if ( MusicUtil.isMusicSuffix( fileEnd ) ){

            log.info( "正在上传歌曲: {}", fileName );
            Music music = musicService.analysisAndUpload(fileName, bytes);


            desMusicInfos.add( getMusicInfoStr(music) );//歌曲信息添加到结果集

        }else if (ZipUtils.isZipFile( fileEnd ) ){
            File tempFile = FIleUtils.createTempFileAndLog( );
            log.info( "上传的文件: {} 为压缩文件,正在分析结构", fileName );

            FileUtil.writeBytes( bytes, tempFile );

            zipUtils.uploadMusicZipFile(tempFile).forEach( music ->
                desMusicInfos.add( getMusicInfoStr(music) )

            );


           FIleUtils.deleteAndLog( tempFile );
        }

        if (desMusicInfos.isEmpty()){
            desMusicInfos.add("上传失败");
        }

        return desMusicInfos;

    }

    @GetMapping(value = "/{fileId}", produces = "audio/mpeg")
    public byte[] getMusic(@PathVariable String fileId){

        GridFsResource gridFsResource = musicService.getMusicFileByFileId(fileId);
        if (gridFsResource != null){
            try {
                return IoUtil.readBytes( gridFsResource.getInputStream() );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return null;
    }

    private String getMusicInfoStr(Music music){
        if ( music.getFileId() != null ){
            return music.getAudioName();
        }else {
            return music.getAudioName() + "已存在,请勿重复上传";
        }
    }

}
