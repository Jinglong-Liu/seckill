package cn.edu.nju.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQHeadersConfig {
    private static final String QUEUE01 = "queue_header01";
    private static final String QUEUE02 = "queue_header02";
    private static final String EXCHANGE = "headExchange";

    @Bean
    public Queue hQueue01(){
        return new Queue(QUEUE01);
    }
    @Bean
    public Queue hQueue02(){
        return new Queue(QUEUE02);
    }
    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(EXCHANGE);
    }
    @Bean
    public Binding hBinding01(){
        Map<String,Object> map = new HashMap<>();
        map.put("color","red");
        map.put("speed","low");
        //whereAny:头部中有任一key
        return BindingBuilder.bind(hQueue01()).to(headersExchange()).whereAny(map).match();
    }
    @Bean
    public Binding hBinding02(){
        Map<String,Object> map = new HashMap<>();
        map.put("color","red");
        map.put("speed","fast");
        //whereAny:头部中有所有key
        return BindingBuilder.bind(hQueue02()).to(headersExchange()).whereAll(map).match();
    }
}
