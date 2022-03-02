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
    public void send03(Object message){
        log.info("发送 03 消息 " + message);
        rabbitTemplate.convertAndSend("topicExchange","queue.red.message",message);
    }
    public void send04(Object message){
        log.info("发送 04 消息 " + message);
        rabbitTemplate.convertAndSend("topicExchange","message.queue.green.abc",message);
    }
    public void send07(String message){
        log.info("发送 07 消息 " + message);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("color","red");
        properties.setHeader("speed","fast");
        Message m = new Message(message.getBytes(),properties);
        rabbitTemplate.convertAndSend("headExchange","",m);
    }
    public void send08(String message){
        log.info("发送 08 消息 " + message);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("color","red");
        properties.setHeader("speed","normal");
        Message m = new Message(message.getBytes(),properties);
        rabbitTemplate.convertAndSend("headExchange","",m);
    }
}
