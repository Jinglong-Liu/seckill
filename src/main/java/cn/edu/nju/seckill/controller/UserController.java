package cn.edu.nju.seckill.controller;


import cn.edu.nju.seckill.pojo.User;
import cn.edu.nju.seckill.vo.RespBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 *
 * @since 2022-02-28
 */
@Controller
@RequestMapping("/user")
public class UserController {
    /**
     * 用于测试
     * @param user
     * @return
     */
    @RequestMapping("info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }
}
