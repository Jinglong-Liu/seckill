package cn.edu.nju.seckill.service.impl;

import cn.edu.nju.seckill.mapper.UserMapper;
import cn.edu.nju.seckill.pojo.User;
import cn.edu.nju.seckill.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2022-02-28
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
