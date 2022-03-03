package cn.edu.nju.seckill;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SeckillApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisScript<Boolean> script;
    @Test
    void contextLoads() {
    }
    @Test
    void testLock01(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Boolean isLock = valueOperations.setIfAbsent("k1","v1");
        if(isLock){
            String name = (String) valueOperations.get("k1");
            System.out.println("name = " + name);
            redisTemplate.delete("k1");
        }
        else{
            System.out.println("有线程在使用，请稍后");
        }
    }
    @Test
    public void testLock02(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Boolean isLock = valueOperations.setIfAbsent("k1","v1",5, TimeUnit.SECONDS);
        if(isLock){
            String name = (String) valueOperations.get("k1");
            System.out.println("name = " + name);
            //redisTemplate.delete("k1");
        }
        else{
            System.out.println("有线程在使用，请稍后");
        }
    }
    @Test
    public void testLock03(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String value = UUID.randomUUID().toString();
        Boolean isLock = valueOperations.setIfAbsent("k1",value,20, TimeUnit.SECONDS);
        if(isLock){
            valueOperations.set("name","xxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name = " + name);
            Object k1 = valueOperations.get("k1");
            System.out.println(k1);
            //redisTemplate.delete("k1");
            Boolean result = (Boolean) redisTemplate.execute(script, Collections.singletonList("k1"),value);
            System.out.println(result);
        }
        else{
            System.out.println("有线程在使用，请稍后");
        }
    }
}
