package com.liuqi.business.dto.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.liuqi.business.model.TradeRecordModelDto;
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
public class OrderRecordRespDto {
    //订单id
    private Long orderId;
    //交易对
    private String symbol;
    private Integer tradeType;
    private String tradeTypeStr;
    //成交数量
    private String quantity;
    //成交价格
    private String price;
    //手续费
    private String rate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;


    public static OrderRecordRespDto transfer(String symbol, Long searchUserId, TradeRecordModelDto record){
        if(record.getSellUserId().equals(searchUserId)){
            return OrderRecordRespDto.builder()
                    .orderId(record.getSellTrusteeId())
                    .symbol(symbol)
                    .tradeType(record.getTradeType())
                    .tradeTypeStr(record.getTradeTypeStr())
                    .quantity(MathUtil.format(record.getTradeQuantity()))
                    .price(MathUtil.format(record.getSellPrice()))
                    .rate(MathUtil.format(record.getSellCharge()))
                    .time(record.getCreateTime())
                    .build();
        }else{
            return OrderRecordRespDto.builder()
                    .orderId(record.getBuyTrusteeId())
                    .symbol(symbol)
                    .tradeType(record.getTradeType())
                    .tradeTypeStr(record.getTradeTypeStr())
                    .quantity(MathUtil.format(record.getTradeQuantity()))
                    .price(MathUtil.format(record.getBuyPrice()))
                    .rate(MathUtil.format(record.getBuyCharge()))
                    .time(record.getCreateTime())
                    .build();
        }
    }

    public static List<OrderRecordRespDto> transferList(String symbol, Long searchUserId, List<TradeRecordModelDto> recordList){
        List<OrderRecordRespDto> list=new ArrayList<OrderRecordRespDto>();
        recordList.stream().forEach(t->{
            list.add(OrderRecordRespDto.transfer(symbol,searchUserId,t));
        });
        return list;
    }
}
