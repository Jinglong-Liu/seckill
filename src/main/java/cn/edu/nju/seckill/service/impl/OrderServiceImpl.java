package cn.edu.nju.seckill.service.impl;

import cn.edu.nju.seckill.mapper.OrderMapper;
import cn.edu.nju.seckill.pojo.Order;
import cn.edu.nju.seckill.pojo.SeckillGoods;
import cn.edu.nju.seckill.pojo.SeckillOrder;
import cn.edu.nju.seckill.pojo.User;
import cn.edu.nju.seckill.service.IGoodsService;
import cn.edu.nju.seckill.service.IOrderService;
import cn.edu.nju.seckill.service.ISeckillGoodsService;
import cn.edu.nju.seckill.service.ISeckillOrderService;
import cn.edu.nju.seckill.vo.GoodsVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2022-03-01
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    ISeckillOrderService seckillOrderService;
    @Autowired
    ISeckillGoodsService seckillGoodsService;

    @Autowired
    OrderMapper orderMapper;

    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goods) {
        // 1、减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>()
                .eq("goods_id",goods.getId()));
        // seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        // seckillGoodsService.updateById(seckillGoods);
        boolean seckillGoodsResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count = stock_count - 1")
                .eq("id", seckillGoods.getId())
                .gt("stock_count", 0));
        if(!seckillGoodsResult){
            return null;
        }
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);

        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setGoodsName(goods.getGoodsName());

        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        //order.setPayDate();

        orderMapper.insert(order);
        // 秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        //seckillOrder.setId();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);

        redisTemplate.opsForValue().set("order:"+user.getId()+":"+goods.getId(),seckillOrder);
        return order;
    }
}
