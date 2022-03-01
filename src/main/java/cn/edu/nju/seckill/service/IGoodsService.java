package cn.edu.nju.seckill.service;

import cn.edu.nju.seckill.pojo.Goods;
import cn.edu.nju.seckill.vo.GoodsVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2022-03-01
 */
public interface IGoodsService extends IService<Goods> {
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
