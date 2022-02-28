package cn.edu.nju.seckill.service.impl;

import cn.edu.nju.seckill.mapper.UserMapper;
import cn.edu.nju.seckill.pojo.User;
import cn.edu.nju.seckill.service.IUserService;
import cn.edu.nju.seckill.utils.MD5Util;
import cn.edu.nju.seckill.utils.ValidatorUtil;
import cn.edu.nju.seckill.vo.LoginVo;
import cn.edu.nju.seckill.vo.RespBean;
import cn.edu.nju.seckill.vo.RespBeanEnum;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    @Autowired
    private UserMapper userMapper;
    @Override
    public RespBean doLogin(LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        if(!ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }
        User user = userMapper.selectById(mobile);
        if(user == null){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        if(!MD5Util.fromPassToDBPass(password,user.getSlat()).equals(user.getPasword())){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        return RespBean.success();
    }
}
