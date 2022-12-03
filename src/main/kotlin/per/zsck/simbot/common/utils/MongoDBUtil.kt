package per.zsck.simbot.common.utils

import com.mongodb.client.result.UpdateResult
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.annotation.Transient

/**
 * @author zsck
 * @date   2022/12/1 - 14:48
 */

fun <T: Any> MongoTemplate.saveOrUpdate(query: Query, entity: T): UpdateResult {

    return entity.javaClass.declaredFields.let {

        upsert( query ,Update().apply {
            it.forEach { field ->
                field.isAccessible = true
                if ( field.getAnnotation( Transient::class.java ) == null)

                    field[ entity ]?.let { value ->
                        this.set( field.name,  value)
                    }

            }

        } , entity.javaClass)

    }

}