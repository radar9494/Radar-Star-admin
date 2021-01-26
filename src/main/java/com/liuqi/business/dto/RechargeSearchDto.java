package com.liuqi.business.dto;

import com.liuqi.business.model.CurrencyModel;
import lombok.Data;

@Data
public class RechargeSearchDto {
    private Long id;
    private String currencyName;
    private String thirdCurrency;
    private Integer protocol;

    private Long curBlock;
    private Integer confirm;

    public RechargeSearchDto() {
    }

    public RechargeSearchDto(Long id,String currencyName, Integer protocol, String thirdCurrency,Integer confirm) {
        this.id = id;
        this.currencyName = currencyName;
        this.protocol = protocol;
        this.thirdCurrency = thirdCurrency;
        this.confirm = confirm;
    }
}
