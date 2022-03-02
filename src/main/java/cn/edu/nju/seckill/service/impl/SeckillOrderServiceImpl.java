package cn.edu.nju.seckill.service.impl;

import cn.edu.nju.seckill.mapper.OrderMapper;
import cn.edu.nju.seckill.mapper.SeckillGoodsMapper;
import cn.edu.nju.seckill.mapper.SeckillOrderMapper;
import cn.edu.nju.seckill.pojo.SeckillOrder;
import cn.edu.nju.seckill.pojo.User;
import cn.edu.nju.seckill.service.ISeckillOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2022-03-01
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillOrderMapper orderMapper;
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = orderMapper.selectOne(new QueryWrapper<SeckillOrder>()
                .eq("user_id", user.getId())
                .eq("goods_id", goodsId));
        if(null!=seckillOrder){
            return seckillOrder.getOrderId();
        } else if(redisTemplate.hasKey("isStockEmpty:" + goodsId)){
            return -1L;
        }
        return 0L;

    }
}
