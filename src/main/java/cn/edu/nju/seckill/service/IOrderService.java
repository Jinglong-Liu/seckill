package cn.edu.nju.seckill.service;

import cn.edu.nju.seckill.pojo.Order;
import cn.edu.nju.seckill.pojo.User;
import cn.edu.nju.seckill.vo.GoodsVo;
import cn.edu.nju.seckill.vo.OrderDetailVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2022-03-01
 */
public interface IOrderService extends IService<Order> {

    Order seckill(User user, GoodsVo goodsVo);

    OrderDetailVo detail(Long orderId);
}
