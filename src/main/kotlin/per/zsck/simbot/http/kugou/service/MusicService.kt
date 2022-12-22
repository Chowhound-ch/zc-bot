package per.zsck.simbot.http.kugou.service

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import per.zsck.simbot.http.kugou.entity.Music
import java.util.regex.Pattern
import javax.annotation.Resource

/**
 * @author zsck
 * @date   2022/11/10 - 13:04
 */
interface MusicService {
    fun likeMusic(audioName: String): MutableList<Music>
}

@Service
class MusicServiceImpl : MusicService {

    @Resource
    lateinit var mongoTemplate: MongoTemplate

    override fun likeMusic(audioName: String): MutableList<Music> {
        return mongoTemplate.find( Query.query(Criteria.where( "audioNameIndex" )
            .regex( Pattern.compile(".*${audioName}.*", Pattern.CASE_INSENSITIVE) )), Music::class.java )
    }

}