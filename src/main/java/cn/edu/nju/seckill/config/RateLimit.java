package cn.edu.nju.seckill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface RateLimit {
    // 默认每秒放入桶中的token
    double limitNum() default 2;
    //boolean needLogin() default true;
    //int timeout() default 0;
    String name() default "";
    /**
     * 超时时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
