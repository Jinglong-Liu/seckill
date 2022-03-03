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

    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId,String path);

    /**
     * 校验验证码
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
