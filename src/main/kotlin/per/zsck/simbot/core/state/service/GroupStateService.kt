package per.zsck.simbot.core.state.service

import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Example
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import per.zsck.simbot.core.state.GroupStateCache
import per.zsck.simbot.core.state.entity.GroupState
import per.zsck.simbot.core.state.enums.EnumUtil

/**
 * @author zsck
 * @date   2022/11/5 - 11:21
 */
@Repository
interface GroupStateRepository : MongoRepository<GroupState, Long>

interface GroupStateService{
    fun getGroupState(group: String): GroupState

    fun setGroupState(groupState: GroupState): GroupState

    fun getGroupStateByState(enum: Enum<*>): List<GroupState>
    fun list(): List<GroupState>
}
@CacheConfig(cacheNames = ["state"])
@Service
class GroupStateServiceImpl(val repository: GroupStateRepository): GroupStateService{


    @Cacheable(key = "#group")
    override fun getGroupState(group: String): GroupState {

        return  repository.findOne(Example.of(GroupState.of(group))).orElse(GroupState(groupNumber = group))
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
            repository.save(this)
//            saveOrUpdate(this, KtQueryWrapper(GroupState::class.java).eq(GroupState::groupNumber, groupState.groupNumber))
        }
    }

    override fun getGroupStateByState(enum: Enum<*>): List<GroupState> {
        EnumUtil.BOOL_ENUM_MAP[enum::class.java]

        val field = GroupStateCache.getByClass(enum::class.java)


        return repository.findAll(Example.of(
            GroupState.instance().apply {
                field.set(this, enum)
            }
        ))
    }

    override fun list(): List<GroupState> {
        return repository.findAll()
    }

}