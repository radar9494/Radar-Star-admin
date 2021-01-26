package com.liuqi.mq.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;

@Data
public class TopicTradeDto implements Serializable{

    private Long tradeId;
    private Integer type;

    private JSONObject json;

    public TopicTradeDto() {
    }

    public TopicTradeDto(Long tradeId, Integer type,JSONObject json) {
        this.tradeId = tradeId;
        this.type = type;
        this.json = json;
    }
}
