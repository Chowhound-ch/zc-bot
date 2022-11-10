package per.zsck.simbot.core.state.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import per.zsck.simbot.core.state.entity.GroupState

/**
 * @author zsck
 * @date   2022/11/5 - 11:20
 */
@Mapper
interface GroupStateMapper: BaseMapper<GroupState> {
}