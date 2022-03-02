package cn.edu.nju.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQDirectConfig {
    private static final String D_QUEUE1 = "queue_direct01";
    private static final String D_QUEUE2 = "queue_direct02";
    private static final String D_EXCHANGE = "directExchange";
    private static final String ROUTINGKEY01 = "queue.red";
    private static final String ROUTINGKEY02 = "queue.green";

    @Bean
    public Queue dQueue01(){
        return new Queue(D_QUEUE1);
    }
    @Bean
    public Queue dQueue02(){
        return new Queue(D_QUEUE2);
    }
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(D_EXCHANGE);
    }
    @Bean
    public Binding dBinding01(){
        return BindingBuilder.bind(dQueue01()).to(directExchange()).with(ROUTINGKEY01);
    }
    @Bean
    public Binding dBinding02(){
        return BindingBuilder.bind(dQueue02()).to(directExchange()).with(ROUTINGKEY02);
    }
}
