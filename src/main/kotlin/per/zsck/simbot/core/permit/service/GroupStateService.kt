package per.zsck.simbot.core.permit.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.baomidou.mybatisplus.extension.service.IService
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import per.zsck.simbot.core.permit.Permit
import per.zsck.simbot.core.permit.entity.PermitDetail

import per.zsck.simbot.core.permit.mapper.PermitDetailMapper
import javax.annotation.PostConstruct

/**
 * @author zsck
 * @date   2022/11/5 - 11:21
 */
interface PermitDetailService: IService<PermitDetail>{
    fun getPermit(qqNumber: String): PermitDetail

    fun setPermit(qqNumber: String, permit: Permit):Boolean

    fun setUsedHostPermit(qqNumber: String, permit: Permit): Boolean
}

@Service
class PermitDetailServiceImpl: PermitDetailService, ServiceImpl<PermitDetailMapper, PermitDetail>() {


    override fun getPermit(qqNumber: String):PermitDetail {
        return getOne(
            KtQueryWrapper(PermitDetail::class.java).apply{
                this.eq(PermitDetail::qqNumber, qqNumber)
            }
        ) ?: PermitDetail(qqNumber).apply {
            save(this)
        }
    }
    override fun setPermit(qqNumber: String, permit: Permit):Boolean {
        return if (getPermit(qqNumber).permit == permit){
            false
        }else{
            update(KtUpdateWrapper(PermitDetail::class.java).apply {
                this.eq(PermitDetail::qqNumber, qqNumber).set(PermitDetail::permit, permit)
            })
        }
    }

    override fun setUsedHostPermit(qqNumber: String, permit: Permit): Boolean {
        return update(KtUpdateWrapper(PermitDetail::class.java).apply {
            this.eq(PermitDetail::permit, Permit.HOST).not { it.eq(PermitDetail::qqNumber, qqNumber) }.
                    set(PermitDetail::permit, permit)
        })
    }
}