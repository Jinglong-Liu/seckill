package cn.edu.nju.seckill.controller;

import cn.edu.nju.seckill.config.AccessLimit;
import cn.edu.nju.seckill.exception.GlobalException;
import cn.edu.nju.seckill.pojo.Order;
import cn.edu.nju.seckill.pojo.SeckillMessage;
import cn.edu.nju.seckill.pojo.SeckillOrder;
import cn.edu.nju.seckill.pojo.User;
import cn.edu.nju.seckill.rabbitmq.MQSender;
import cn.edu.nju.seckill.service.IGoodsService;
import cn.edu.nju.seckill.service.IOrderService;
import cn.edu.nju.seckill.service.ISeckillOrderService;
import cn.edu.nju.seckill.utils.JsonUtil;
import cn.edu.nju.seckill.vo.GoodsVo;
import cn.edu.nju.seckill.vo.RespBean;
import cn.edu.nju.seckill.vo.RespBeanEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;

import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {

    @Autowired
    IOrderService orderService;
    @Autowired
    IGoodsService goodsService;
    @Autowired
    ISeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisScript<Long>script;

    @Autowired
    private MQSender mqSender;

    private Map<Long,Boolean> emptyStockMap = new HashMap<>();

    @PostMapping("/{path}/doSeckill")
    @ResponseBody
    public RespBean doSeckill(@PathVariable String path, User user, Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();

        boolean check = orderService.checkPath(user,goodsId,path);
        if(!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // 避免重复抢购
        SeckillOrder seckillOrder = (SeckillOrder)valueOperations.get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder!=null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        // 加一层换内存标记，减少redis访问
        if(emptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 预减库存
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        //Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("seckillGoods:" + goodsId),Collections.EMPTY_LIST);
        if(stock < 0){
            emptyStockMap.put(goodsId,true);
            valueOperations.increment("seckillGoods:" + goodsId);//不要变长负数

            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //Order order = orderService.seckill(user,goodsVo);//rabbitmq
        SeckillMessage message = new SeckillMessage(user,goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(message));
        return RespBean.success(0);// 排队中

    }

    /**
     * 秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @GetMapping("/path")
    @ResponseBody
    @AccessLimit(second=5,maxCount=5,needLogin=true)
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }


        //验证码
        boolean check = orderService.checkCaptcha(user,goodsId,captcha);
        if(!check){
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }
        String str = orderService.createPath(user,goodsId);
        return RespBean.success(str);
    }


    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId 成功 -1失败 0排队
     */
    @GetMapping("result")
    @ResponseBody
    public RespBean getResult(User user,Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }

    /**
     * 初始化 加载商品库存
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(),goodsVo.getStockCount());
            emptyStockMap.put(goodsVo.getId(),false);//有库存
        });
    }
    /**
     * 验证码
     */
    @GetMapping("/captcha")
    public void verifyCode(User user, Long goodsId, HttpServletResponse response){
        if(user == null || goodsId < 0){
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        response.setContentType("image/jpg");
        response.setHeader("Pargam","No-cache");//每次获取新的验证码
        response.setHeader("Cache-control","No-cache");
        //验证码 redis 失效时间
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32,3);
        redisTemplate.opsForValue().set("captcha:" + user.getId()+":" + goodsId,captcha.text(),300, TimeUnit.SECONDS);

        try {
            captcha.out(response.getOutputStream());  // 输出验证码
        } catch (IOException e) {
            e.printStackTrace();
            log.error("验证码生成失败",e.getMessage());
        }
    }
}
