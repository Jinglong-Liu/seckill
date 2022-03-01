package cn.edu.nju.seckill.service.impl;

import cn.edu.nju.seckill.mapper.OrderMapper;
import cn.edu.nju.seckill.pojo.Order;
import cn.edu.nju.seckill.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

}
