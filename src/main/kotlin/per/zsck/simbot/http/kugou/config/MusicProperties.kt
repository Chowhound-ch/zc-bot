package per.zsck.simbot.http.kugou.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author zsck
 * @date   2022/11/12 - 9:40
 */
//@Component
@ConfigurationProperties(prefix = "zsck.kugou", ignoreInvalidFields = true)
class MusicProperties {
    lateinit var addMusicMapping: String
    lateinit var searchMusicMapping: String
}