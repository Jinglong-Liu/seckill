package cn.edu.nju.seckill.vo;

import cn.edu.nju.seckill.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDetailVo {
    private Order order;
    private GoodsVo goodsVo;
}
