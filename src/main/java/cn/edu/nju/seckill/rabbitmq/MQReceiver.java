package cn.edu.nju.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * 消息消费者
 */
@Service
@Slf4j
public class MQReceiver {
    @RabbitListener(queues = "queue")
    public void receive(Object message){
        log.info("接受消息 "+message);
    }

    @RabbitListener(queues = "queue_fanout01")
    public void receive01(Object message){
        log.info("QUEUE01 接受消息：" + message);
    }

    @RabbitListener(queues = "queue_fanout02")
    public void receive02(Object message){
        log.info("QUEUE02 接受消息：" + message);
    }
    @RabbitListener(queues = "queue_direct01")
    public void receive03(Object message){
        log.info("QUEUE03 接受消息：" + message);
    }
    @RabbitListener(queues = "queue_direct02")
    public void receive04(Object message){
        log.info("QUEUE04 接受消息：" + message);
    }
}
