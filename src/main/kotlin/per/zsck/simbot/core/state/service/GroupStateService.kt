package per.zsck.simbot.core.state.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.IService
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import per.zsck.simbot.core.state.GroupStateCache
import per.zsck.simbot.core.state.entity.GroupState
import per.zsck.simbot.core.state.enums.EnumUtil
import per.zsck.simbot.core.state.mapper.GroupStateMapper

/**
 * @author zsck
 * @date   2022/11/5 - 11:21
 */
interface GroupStateService: IService<GroupState>{
    fun getGroupState(group: String): GroupState

    fun setGroupState(groupState: GroupState): GroupState

    fun getGroupStateByState(enum: Enum<*>): List<GroupState>
}
@CacheConfig(cacheNames = ["state"])
@Service
class GroupStateServiceImpl: GroupStateService, ServiceImpl<GroupStateMapper, GroupState>(){


    @Cacheable(key = "#group")
    override fun getGroupState(group: String): GroupState {
        return getOne(
            KtQueryWrapper(GroupState::class.java).apply {
                this.eq(GroupState::groupNumber, group)
            }
        )
    }

    /**
     * true,状态改变, false,状态未改变
     */
    @CachePut(key = "#groupState.groupNumber")
    override fun setGroupState(groupState: GroupState): GroupState {
        if (getGroupState(groupState.groupNumber!!) == groupState) {
            return groupState
        }

        return groupState.apply {
            saveOrUpdate(this, KtQueryWrapper(GroupState::class.java).eq(GroupState::groupNumber, groupState.groupNumber))

        }
    }

    override fun getGroupStateByState(enum: Enum<*>): List<GroupState> {
        EnumUtil.BOOL_ENUM_MAP[enum::class.java]

        val field = GroupStateCache.getByClass(enum::class.java)


        return this.list(QueryWrapper<GroupState>().eq(field.name, enum))
    }

}