package com.liuqi.business.dto.api.response;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.liuqi.business.model.TrusteeModelDto;
import com.liuqi.utils.MathUtil;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author tanyan
 * @create 2020-07=20
 * @description
 */
@Builder
@Data
public class OrderRespDto {
    //订单id
    private Long orderId;
    //交易对
    private String symbol;
    //交易类型
    private Integer tradeType;
    private String tradeTypeStr;
    //数量
    private String quantity;
    //价格
    private String price;
    //已成交数量
    private String tradeQuantity;
    //状态
    private Integer status;
    private String statusStr;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;



    public static OrderRespDto transfer(String symbol, TrusteeModelDto trustee){
        return  OrderRespDto.builder()
                .orderId(trustee.getId())
                .symbol(symbol)
                .tradeType(trustee.getTradeType())
                .tradeTypeStr(trustee.getTradeTypeStr())
                .quantity(MathUtil.format(trustee.getQuantity()))
                .price(MathUtil.format(trustee.getPrice()))
                .tradeQuantity(MathUtil.format(trustee.getTradeQuantity()))
                .time(trustee.getCreateTime())
                .status(trustee.getStatus())
                .statusStr(trustee.getStatusStr())
                .build();
    }


    public static List<OrderRespDto> transferList(String symbol, List<TrusteeModelDto> trusteeList){
        List<OrderRespDto> list=new ArrayList<OrderRespDto>();
        trusteeList.stream().forEach(t->{
            list.add(OrderRespDto.transfer(symbol,t));
        });
        return list;
    }
}
