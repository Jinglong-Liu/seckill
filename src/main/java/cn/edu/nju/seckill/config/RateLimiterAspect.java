package cn.edu.nju.seckill.config;

import cn.edu.nju.seckill.exception.GlobalException;
import cn.edu.nju.seckill.vo.RespBeanEnum;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Aspect
@Component
public class RateLimiterAspect {
    private ConcurrentHashMap<String, RateLimiter> RATE_LIMITER  = new ConcurrentHashMap<>();
    private RateLimiter rateLimiter;

    @Pointcut("@annotation(cn.edu.nju.seckill.config.RateLimit)")
    public void serviceLimit() {
    }

    @Around("serviceLimit()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        //获取拦截的方法名
        Signature sig = point.getSignature();
        //获取拦截的方法名
        MethodSignature msig = (MethodSignature) sig;
        //返回被织入增加处理目标对象
        Object target = point.getTarget();
        //为了获取注解信息
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        //获取注解信息
        RateLimit annotation = currentMethod.getAnnotation(RateLimit.class);
        double limitNum = annotation.limitNum(); //获取注解每秒加入桶中的token
        String functionName = msig.getName(); // 注解所在方法名区分不同的限流策略

        if(RATE_LIMITER.containsKey(functionName)){
            rateLimiter=RATE_LIMITER.get(functionName);
        }else {
            RATE_LIMITER.put(functionName, RateLimiter.create(limitNum));
            rateLimiter=RATE_LIMITER.get(functionName);
        }
        if(rateLimiter.tryAcquire()) {
            //log.info("处理完成");
            return point.proceed();
        } else {
            throw new GlobalException(RespBeanEnum.ACCESS_LIMIT_REAHCED);
        }
    }
}
