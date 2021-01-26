package com.liuqi.business.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class RecordDto implements Serializable{
    //时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date date;
    //类型
    private Integer tradeType;
    //价格
    private BigDecimal price;
    //数量
    private BigDecimal num;
}
