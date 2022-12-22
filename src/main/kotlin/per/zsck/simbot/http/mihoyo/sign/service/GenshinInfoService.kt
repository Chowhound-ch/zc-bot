package per.zsck.simbot.http.mihoyo.sign.service

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import per.zsck.simbot.common.utils.saveOrUpdate
import per.zsck.simbot.http.mihoyo.sign.entity.GenshinInfo
import javax.annotation.Resource

/**
 * @author zsck
 * @date   2022/10/31 - 20:14
 */

interface GenshinInfoService{
    fun getGenshinInfo(uid: String): GenshinInfo?

    /**
     * 根据[GenshinInfo.uid]更新库中数据，库中有则更新，无则插入
     */
    fun saveGenshinInfo(info: GenshinInfo): Boolean

    /**
     * 根据[GenshinInfo.uid]更新库中数据，库中有则更新，无则插入
     * 入库之前将[GenshinInfo] 设置对应的 qqNumber
     */
    fun saveGenshinInfo(info: GenshinInfo, qqNumber: String): Boolean

    fun getGenshinInfoList(qqNumber: String): List<GenshinInfo>

    fun removeGenshinInfo(uid: String): Boolean
    fun list(): List<GenshinInfo>
}

@Service
class GenshinInfoServiceImpl : GenshinInfoService{
    @Resource
    lateinit var mongoTemplate: MongoTemplate



    override fun getGenshinInfo(uid: String): GenshinInfo? {

        return mongoTemplate.findOne(Query.query(Criteria.where("uid").`is`(uid)), GenshinInfo::class.java)

    }

    override fun saveGenshinInfo(info: GenshinInfo): Boolean {

        return mongoTemplate.saveOrUpdate(Query.query(Criteria.where("uid").`is`( info.uid )), info).wasAcknowledged()

    }

    override fun saveGenshinInfo(info: GenshinInfo, qqNumber: String): Boolean {
        info.qqNumber = qqNumber
        return saveGenshinInfo(info)
    }

    override fun getGenshinInfoList(qqNumber: String): List<GenshinInfo> {
        return  mongoTemplate.find( Query.query(Criteria.where( "qqNumber" ).`is`( qqNumber )) , GenshinInfo::class.java)

    }

    override fun removeGenshinInfo(uid: String): Boolean {
        return mongoTemplate.remove( Query.query(Criteria.where("uid").`is`( uid )), GenshinInfo::class.java ).wasAcknowledged()
    }

    override fun list(): List<GenshinInfo> {
        return mongoTemplate.findAll(GenshinInfo::class.java)
    }
}