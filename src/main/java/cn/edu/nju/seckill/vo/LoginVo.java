package cn.edu.nju.seckill.vo;

import cn.edu.nju.seckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class LoginVo {

    @IsMobile // 自定义注解
    private String mobile;
    @NotNull
    @Length(min = 32)
    private String password;
}
