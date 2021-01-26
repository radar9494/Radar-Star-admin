package com.liuqi.business.dto.chain;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class ThirdBaseDto implements Serializable{
    /**
     * 订单号
     */
    @NotNull(message = "商户号不能为空")
    @Length(max = 50,message = "商户号最大50字符")
    private String storeNo;

    /**
     * 订单号
     */
    @NotNull(message = "订单号不能为空")
    @Length(max = 20,message = "订单号最大20字符")
    private String no;
    /**
     * 币种
     */
    @NotNull(message = "币种不能为空")
    @Length(max = 50,message = "币种最大50字符")
    private String currency;
    /**
     * 签名
     */
    @NotNull(message = "签名不能为空")
    @Length(max = 100,message = "签名最大100字符")
    private String sign;
}
