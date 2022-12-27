package per.zsck.music.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;


/**
 * @author zsck
 * @date 2022/12/11 - 15:27
 */
@Configuration
public class MongoConfig {
    @Value("${spring.data.mongodb.database}")
    String db;

    //GridFSBucket用于打开下载流
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public GridFSBucket getGridFSBucket(MongoClient mongoClient){


        return GridFSBuckets.create( mongoClient.getDatabase(db) );
    }


}
