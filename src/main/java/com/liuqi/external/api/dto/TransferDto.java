package com.liuqi.external.api.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TransferDto implements Serializable {

    @NotNull(message = "num不能为空")
    private String num;

    @NotNull(message = "name不能为空")
    @Length(max = 20,message = "name不能超过20")
    private String name;

    @NotNull(message = "userName不能为空")
    @Length(max = 40,message = "userName不能超过40")
    private String userName;

    @NotNull(message = "currencyName不能为空")
    @Length(max = 20,message = "currencyName不能超过20")
    private String currencyName;

    @NotNull(message = "quantity不能为空")
    private BigDecimal quantity;

    private int type=0;

    @NotNull(message = "transferName不能为空")
    @Length(max = 20,message = "transferName不能超过20")
    private String transferName;

    @NotNull(message = "sign不能为空")
    private String sign;
}
