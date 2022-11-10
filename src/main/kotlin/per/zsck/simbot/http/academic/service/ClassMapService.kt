package per.zsck.simbot.http.academic.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.IService
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service
import per.zsck.simbot.http.academic.entity.ClassMap
import per.zsck.simbot.http.academic.mapper.ClassMapMapper

/**
 * @author zsck
 * @date   2022/11/8 - 15:44
 */
interface ClassMapService: IService<ClassMap> {
    fun removeAll(): Long

    fun likeClassName(name: String): List<ClassMap>
}
@Service
class ClassMapServiceImpl: ClassMapService, ServiceImpl<ClassMapMapper, ClassMap>(){
    override fun removeAll(): Long {
        return count().also { remove(null) }
    }

    override fun likeClassName(name: String): List<ClassMap> {
        return list(KtQueryWrapper(ClassMap::class.java).like(ClassMap::className, name))
    }
}