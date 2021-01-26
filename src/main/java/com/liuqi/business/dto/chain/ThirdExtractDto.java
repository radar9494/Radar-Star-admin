package com.liuqi.business.dto.chain;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ThirdExtractDto extends ThirdBaseDto implements Serializable {



    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0",message = "数量不能小于0")
    private BigDecimal quantity;

    /**
     * 地址
     */
    @NotNull(message = "地址不能为空")
    @Length(max = 100,message = "地址最大100字符")
    private String address;

    /**
     * 标签
     */
    @Length(max = 100,message = "标签最大100字符")
    private String memo;


}
