package cn.edu.nju.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/demo")
public class DemoController {
    @RequestMapping("/hello")
    private String hello(Model model){
        model.addAttribute("name", "zhang san");
        return "hello";
    }
}
