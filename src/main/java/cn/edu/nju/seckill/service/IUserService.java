package cn.edu.nju.seckill.service;

import cn.edu.nju.seckill.pojo.User;
import cn.edu.nju.seckill.vo.LoginVo;
import cn.edu.nju.seckill.vo.RespBean;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2022-02-28
 */
public interface IUserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo);
}
