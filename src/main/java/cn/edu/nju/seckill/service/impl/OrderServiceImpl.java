package cn.edu.nju.seckill.service.impl;

import cn.edu.nju.seckill.exception.GlobalException;
import cn.edu.nju.seckill.mapper.OrderMapper;
import cn.edu.nju.seckill.pojo.Order;
import cn.edu.nju.seckill.pojo.SeckillGoods;
import cn.edu.nju.seckill.pojo.SeckillOrder;
import cn.edu.nju.seckill.pojo.User;
import cn.edu.nju.seckill.service.IGoodsService;
import cn.edu.nju.seckill.service.IOrderService;
import cn.edu.nju.seckill.service.ISeckillGoodsService;
import cn.edu.nju.seckill.service.ISeckillOrderService;
import cn.edu.nju.seckill.utils.MD5Util;
import cn.edu.nju.seckill.utils.UUIDUtil;
import cn.edu.nju.seckill.vo.GoodsVo;
import cn.edu.nju.seckill.vo.OrderDetailVo;
import cn.edu.nju.seckill.vo.RespBeanEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    IGoodsService goodsService;

    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 1、减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>()
                .eq("goods_id",goods.getId()));
        // seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        // seckillGoodsService.updateById(seckillGoods);
        boolean seckillGoodsResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count = stock_count - 1")
                .eq("id", seckillGoods.getId())
                .gt("stock_count", 0));
        /*if(!seckillGoodsResult){
            return null;
        }*/
        // 判断是否有库存。

        if(seckillGoods.getStockCount() < 0){
            valueOperations.set("isStockEmpty:" + goods.getId(),"0");
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

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailVo detail(Long orderId) {
        if(orderId == null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detailVo = new OrderDetailVo();
        detailVo.setOrder(order);
        detailVo.setGoodsVo(goodsVo);
        return detailVo;
    }

    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() +":"+ goodsId,str,60, TimeUnit.SECONDS);
        return str;
    }

    /**
     * 校验秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public boolean checkPath(User user, Long goodsId,String path) {
        if(user == null || goodsId < 0 || StringUtils.isEmpty(path)){
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }
}
