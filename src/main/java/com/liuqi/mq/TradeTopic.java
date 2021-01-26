package com.liuqi.mq;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.dto.TradeInfoDto;
import com.liuqi.business.enums.WebSocketTypeEnum;
import com.liuqi.business.service.IndexService;
import com.liuqi.business.service.TradeInfoCacheService;
import com.liuqi.business.websocket.TradeWebSocketServer;
import com.liuqi.response.ReturnResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tradeTopic")
public class TradeTopic {
    @Autowired
    private TradeInfoCacheService tradeInfoCacheService;
    @Autowired
    private IndexService indexService;
    /**
     * 推送订阅消息
     * @param tradeId
     */
    public void sendTradeMessage(Long tradeId) {
        JSONObject json = tradeInfoCacheService.getTradeInfo(tradeId);
        ReturnResponse response = ReturnResponse.builder().code(WebSocketTypeEnum.TRADE.getCode()).obj(json).time(System.currentTimeMillis()).build();
        TradeWebSocketServer.sendTradeMessage(tradeId, JSONObject.toJSONString(response));
        this.sendDeptMessage(tradeId);
    }

    /**
     * 推送订阅消息
     * @param tradeId
     */
    public void sendDeptMessage(Long tradeId) {
        JSONObject json = tradeInfoCacheService.getTradeDepthInfo(tradeId,null);
        ReturnResponse response = ReturnResponse.builder().code(WebSocketTypeEnum.DEPTH.getCode()).obj(json).time(System.currentTimeMillis()).build();
        TradeWebSocketServer.sendDeepMessage(tradeId, JSONObject.toJSONString(response));
    }

    /**
     * 推送订阅消息
     *
     * @param areaId
     */
    public void sendAreaMessage(Long areaId) {
        JSONObject json = new JSONObject();
        List<TradeInfoDto> dtoList = indexService.getByAreaId(areaId);
        json.put("tradeList", dtoList);
        json.put("areaId", areaId);
        ReturnResponse response = ReturnResponse.builder().code(WebSocketTypeEnum.AREA.getCode()).obj(json).time(System.currentTimeMillis()).build();
        TradeWebSocketServer.sendAreaMessage(areaId, JSONObject.toJSONString(response));
    }
}
