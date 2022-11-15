package per.zsck.simbot.http.kugou.config

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import per.zsck.simbot.common.logInfo
import per.zsck.simbot.http.kugou.entity.Music
import per.zsck.simbot.http.kugou.service.MusicService

/**
 * @author zsck
 * @date   2022/11/12 - 9:15
 */
@ConditionalOnProperty(prefix = "zsck.kugou", value = ["enable-local"], havingValue = "true")
@EnableConfigurationProperties(MusicProperties::class)
@Configuration
class MusicConfig(
    val musicProperties: MusicProperties
) {

    @Aspect
    @Configuration
    class MusicProxy(
        val musicService: MusicService
    ){

        @Around("execution(* per.zsck.simbot.http.kugou.KuGouMusic.getSearchRes(..))")
        fun ProceedingJoinPoint.musicProxy(): List<*> {
            logInfo("正在从已有资源中寻找关键字为: {}的歌曲", args[0])

            val localMusicList = musicService.likeMusic(args[0] as String)
            val need = args[1] as Int

            if ( need == 1 && localMusicList.size >= 1){//只搜索一条记录,优先返回本地数据
                return mutableListOf( localMusicList[0] )
            }


            val localMusicMaxSize =(if (need > 5) need - 5 else 0).let { // 搜索5条以上则需要从本地获取5条以上的记录
                if (it > localMusicList.size){  //如果本地记录数不够再从网络获取
                    return@let localMusicList.size
                }else{
                    return@let it
                }
            }

            val searchRes = proceed(arrayOf(args[0], need - localMusicMaxSize)) as MutableList<*>?

            logInfo("搜索{}条音乐, 关键字为{},从本地获取{}条记录", args[1], args[0], localMusicMaxSize)


            return searchRes?.let { it + localMusicList } ?: ArrayList<Music>()
        }

    }
}