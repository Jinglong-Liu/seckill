package cn.edu.nju.seckill.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * rabbit mq 生产者 消费者
 */
@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue queue(){
        return new Queue("queue",true);
    }

}
