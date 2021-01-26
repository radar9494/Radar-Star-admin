package com.liuqi.business.dto.stat;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CtcStatShowDto implements Serializable {

    //币种
    private String currencyName;
    //商户名字
    private String storeName;
    //商户名字
    private String realName;
    //类型
    private String tradeTypeStr;
    //状态名称
    private String statusStr;

    private BigDecimal waitMoney=BigDecimal.ZERO; //0
    private BigDecimal runingMoney=BigDecimal.ZERO;//1
    private BigDecimal endMoney=BigDecimal.ZERO;//2
    private BigDecimal cancelMoney=BigDecimal.ZERO;//3
}
