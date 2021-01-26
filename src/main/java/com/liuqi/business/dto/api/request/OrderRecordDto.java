package com.liuqi.business.dto.api.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author tanyan
 * @create 2020-07=20
 * @description
 */
@Data
public class OrderRecordDto {
    //交易对
    @NotNull(message = "交易对不为空")
    private String symbol;

    @NotNull(message = "交易类型不能为空")
    private Integer tradeType;

    private Date startTime;

    private Date endTime;

    private Integer pageSize=20;

    private Integer pageNum=1;

    private Long userId;
}
