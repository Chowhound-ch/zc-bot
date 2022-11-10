package per.zsck.simbot.http.mihoyo.sign.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.IService
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service
import per.zsck.simbot.http.mihoyo.sign.entity.GenshinInfo
import per.zsck.simbot.http.mihoyo.sign.mapper.GenshinInfoMapper

/**
 * @author zsck
 * @date   2022/10/31 - 20:14
 */
interface GenshinInfoService: IService<GenshinInfo>{
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
}

@Service
class GenshinInfoServiceImpl: GenshinInfoService, ServiceImpl<GenshinInfoMapper, GenshinInfo>(){
    override fun getGenshinInfo(uid: String): GenshinInfo? {

        return getOne(KtQueryWrapper(GenshinInfo::class.java).eq(GenshinInfo::uid, uid))
    }

    override fun saveGenshinInfo(info: GenshinInfo): Boolean {
        return saveOrUpdate(info, KtQueryWrapper(GenshinInfo::class.java).eq(GenshinInfo::uid, info.uid))
    }

    override fun saveGenshinInfo(info: GenshinInfo, qqNumber: String): Boolean {
        info.qqNumber = qqNumber
        return saveGenshinInfo(info)
    }

    override fun getGenshinInfoList(qqNumber: String): List<GenshinInfo> {
        return list(KtQueryWrapper(GenshinInfo::class.java).eq(GenshinInfo::qqNumber, qqNumber))
    }

    override fun removeGenshinInfo(uid: String): Boolean {
        return remove( KtQueryWrapper(GenshinInfo::class.java).eq(GenshinInfo::uid, uid) )
    }
}