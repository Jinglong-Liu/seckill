package cn.edu.nju.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQSender {
    @Autowired
    RabbitTemplate rabbitTemplate;

    public void send(Object message){
        log.info("发送消息 " + message);
        rabbitTemplate.convertAndSend("queue",message);
        rabbitTemplate.convertAndSend("fanoutExchange","",message);
    }
    public void send01(Object message){
        log.info("发送 red 消息 " + message);
        rabbitTemplate.convertAndSend("directExchange","queue.red",message);
    }
    public void send02(Object message){
        log.info("发送 green 消息 " + message);
        rabbitTemplate.convertAndSend("directExchange","queue.green",message);
    }
}
