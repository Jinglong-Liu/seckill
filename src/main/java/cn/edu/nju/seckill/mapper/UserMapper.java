package cn.edu.nju.seckill.mapper;

import cn.edu.nju.seckill.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhoubin
 * @since 2022-02-28
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
