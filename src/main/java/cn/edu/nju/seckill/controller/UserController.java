package cn.edu.nju.seckill.controller;


import cn.edu.nju.seckill.pojo.User;
import cn.edu.nju.seckill.rabbitmq.MQSender;
import cn.edu.nju.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    MQSender mqSender;
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

    /**
     * 测试发送rabbit mq 消息
     */
    @RequestMapping("/mq")
    @ResponseBody
    public void mq(){
        mqSender.send("hello");
    }

    @RequestMapping("/mq/fanout")
    @ResponseBody
    public void mqFanout(){
        mqSender.send("hello fanout");
    }

    @RequestMapping("/mq/direct01")
    @ResponseBody
    public void mqDirect01(){
        mqSender.send01("hello red");
    }

    @RequestMapping("/mq/direct02")
    @ResponseBody
    public void mqDirect02(){
        mqSender.send02("hello green");
    }
}
