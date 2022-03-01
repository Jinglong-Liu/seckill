package cn.edu.nju.seckill.mapper;

import cn.edu.nju.seckill.pojo.Goods;
import cn.edu.nju.seckill.vo.GoodsVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhoubin
 * @since 2022-03-01
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
    List<GoodsVo> findGoodsVo();
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
