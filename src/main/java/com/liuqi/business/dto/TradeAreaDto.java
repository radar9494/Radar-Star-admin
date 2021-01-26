package com.liuqi.business.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TradeAreaDto implements Serializable{

    //交易区名称
    private String name;
    //交易对
    private List<TradeInfoDto> trade;

}
