package cn.edu.nju.seckill.config;

import cn.edu.nju.seckill.pojo.User;
import cn.edu.nju.seckill.service.IUserService;
import cn.edu.nju.seckill.utils.CookieUtil;
import cn.edu.nju.seckill.vo.RespBean;
import cn.edu.nju.seckill.vo.RespBeanEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.cfg.defs.CreditCardNumberDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Component
public class AccessLimitInterceptor implements HandlerInterceptor {
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            User user = getUser(request,response);
            UserContext.setUser(user);//
            HandlerMethod hm = (HandlerMethod)handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null){
                return true;
            }
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if(needLogin){
                if(user == null){
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
                key += ":" + user.getId();
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = ((Integer) valueOperations.get(key));
            if(count == null){
                valueOperations.set(key,1,second, TimeUnit.SECONDS);
            }
            else if(count < maxCount){
                valueOperations.increment(key);
            }
            else{
                render(response,RespBeanEnum.ACCESS_LIMIT_REAHCED);
                return false;
            }
        }
        return true;
    }

    /**
     * 构建返回对象
     * @param response
     * @param respBeanEnum
     */
    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws Exception{
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        RespBean respBean = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(respBean));
        out.flush();
        out.close();
    }

    /**
     * 获取当前登录用户
     * @param request
     * @param response
     * @return
     */
    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        if(StringUtils.isEmpty(userTicket)){
            return null;
        }
        return userService.getUserByCookie(userTicket,request,response);
    }
}