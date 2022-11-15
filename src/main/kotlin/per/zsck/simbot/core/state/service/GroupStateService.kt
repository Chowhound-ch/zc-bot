package per.zsck.simbot.core.state.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.IService
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Service
import per.zsck.simbot.common.utils.BeanUtil
import per.zsck.simbot.core.state.GroupStateCache
import per.zsck.simbot.core.state.GroupStateEnum
import per.zsck.simbot.core.state.entity.GroupState
import per.zsck.simbot.core.state.mapper.GroupStateMapper

/**
 * @author zsck
 * @date   2022/11/5 - 11:21
 */
interface GroupStateService: IService<GroupState>{
    fun getGroupState(group: String): GroupState

    fun setGroupStateAndCache(group: String, groupStateEnum: GroupStateEnum): Boolean
}

@Service
class GroupStateServiceImpl: GroupStateService, ServiceImpl<GroupStateMapper, GroupState>(), ApplicationRunner {
    lateinit var groupStateCache: GroupStateCache

    override fun getGroupState(group: String): GroupState {
        return getOne(
            KtQueryWrapper(GroupState::class.java).apply {
                this.eq(GroupState::groupNumber, group)
            }
        )
    }

    override fun setGroupStateAndCache(group: String, groupStateEnum: GroupStateEnum): Boolean {
        return if (groupStateCache.setGroupState(group, groupStateEnum)) {

            saveOrUpdate(GroupState(groupNumber = group, state = groupStateEnum),
                KtQueryWrapper(GroupState::class.java).eq(GroupState::groupNumber, group))

        }else{
            false
        }
    }


    override fun run(args: ApplicationArguments?) {
        groupStateCache = BeanUtil.getBean(GroupStateCache::class.java)
    }
}