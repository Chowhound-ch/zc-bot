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

    fun getGroupListEnableLessonPush(isPush: Int): MutableList<GroupState>

    fun setGroupLessonPush(group: String, isPush: Int): Boolean
}

@Service
class GroupStateServiceImpl: GroupStateService, ServiceImpl<GroupStateMapper, GroupState>(), ApplicationRunner {
    lateinit var groupStateCache: GroupStateCache



    override fun getGroupState(group: String): GroupState {
        return getOne(
            KtQueryWrapper(GroupState::class.java).apply {
                this.eq(GroupState::groupNumber, group)
            }
        ) ?: let {
            GroupState(groupNumber = group).apply { it.save(this) }//没有则保存
        }
    }

    override fun setGroupStateAndCache(group: String, groupStateEnum: GroupStateEnum): Boolean {
        return if (groupStateCache.setGroupState(group, groupStateEnum)) {

            saveOrUpdate(GroupState(groupNumber = group, state = groupStateEnum),
                KtQueryWrapper(GroupState::class.java).eq(GroupState::groupNumber, group))

        }else{
            false
        }
    }

    override fun getGroupListEnableLessonPush(isPush: Int): MutableList<GroupState> {
        return list(KtQueryWrapper(GroupState::class.java).eq(GroupState::lessonPush, isPush))
    }

    override fun setGroupLessonPush(group: String, isPush: Int): Boolean {
        return update(getGroupState(group).apply {

                    if (this.lessonPush == isPush){
                        return false
                    }
                    this.lessonPush = isPush
                },
            KtQueryWrapper(GroupState::class.java).eq(GroupState::groupNumber, group))
    }


    override fun run(args: ApplicationArguments?) {
        groupStateCache = BeanUtil.getBean(GroupStateCache::class.java)
    }
}