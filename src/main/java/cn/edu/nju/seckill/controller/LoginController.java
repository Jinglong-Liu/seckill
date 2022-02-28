package cn.edu.nju.seckill.controller;

import cn.edu.nju.seckill.service.IUserService;
import cn.edu.nju.seckill.vo.LoginVo;
import cn.edu.nju.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("login")
@Slf4j
public class LoginController {

    @Autowired
    private IUserService service;

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(LoginVo loginVo){
        //log.info("{}",loginVo);
        return service.doLogin(loginVo);
    }
}
