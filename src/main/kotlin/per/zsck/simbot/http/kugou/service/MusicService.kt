package per.zsck.simbot.http.kugou.service

import com.baomidou.mybatisplus.extension.service.IService
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service
import per.zsck.simbot.http.kugou.entity.Music
import per.zsck.simbot.http.kugou.mapper.MusicMapper

/**
 * @author zsck
 * @date   2022/11/10 - 13:04
 */
interface MusicService: IService<Music>{
    fun likeMusic(audioName: String): MutableList<Music>
}

@Service
class MusicServiceImpl: MusicService, ServiceImpl<MusicMapper, Music>(){
    override fun likeMusic(audioName: String): MutableList<Music> {
        return baseMapper.likeAudioName(audioName)
    }

}