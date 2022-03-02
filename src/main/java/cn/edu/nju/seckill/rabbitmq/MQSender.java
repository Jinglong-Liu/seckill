package cn.edu.nju.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQSender {
    @Autowired
    RabbitTemplate rabbitTemplate;
    /**
     * 发送秒杀信息
     * @param message
     */
    public void sendSeckillMessage(String message){
        System.out.println("发送消息 " + message);
        rabbitTemplate.convertAndSend("seckillExchange","seckill.message",message);
    }
}
