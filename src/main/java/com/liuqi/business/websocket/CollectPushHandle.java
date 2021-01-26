package com.liuqi.business.websocket;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.liuqi.business.dto.TradeInfoDto;
import com.liuqi.business.enums.WebSocketTypeEnum;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.business.service.TradeService;
import com.liuqi.business.service.UserTradeCollectService;
import com.liuqi.response.ReturnResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yeauty.pojo.Session;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description: 收藏区域推送
 * date: 2020/5/23 10:08 <br>
 * author: chenX <br>
 * version: 1.0 <br>
 */
@Service
@Slf4j
public class CollectPushHandle {

    private static ConcurrentHashMap<Long, Session> sessionMap = new ConcurrentHashMap<>();


    private UserTradeCollectService userTradeCollectService;
    private TradeService tradeService;
    private CurrencyTradeService currencyTradeService;

    @Autowired
    public CollectPushHandle(UserTradeCollectService userTradeCollectService, TradeService tradeService, CurrencyTradeService currencyTradeService) {
        this.userTradeCollectService = userTradeCollectService;
        this.tradeService = tradeService;
        this.currencyTradeService = currencyTradeService;
    }

    public ConcurrentHashMap<Long, Session> getMap() {
        return sessionMap;
    }

    public void remove(Session session) {
        sessionMap.values().remove(session);
    }

    public void add(long userId, Session session) {
        sessionMap.put(userId, session);
        sendMessage(session, pushUser(userId), WebSocketTypeEnum.OPTIONAL_AREA.getCode());
    }

    public void pushAll() {
        Map<Long, TradeInfoDto> map = Maps.newHashMap();
        sessionMap.forEach((k, v) -> {
            List<Long> collect = userTradeCollectService.getByUserId(k);
            List<TradeInfoDto> list = Lists.newArrayList();
            collect.forEach(e -> {
                TradeInfoDto tradeInfoDto = map.get(e);
                if (tradeInfoDto == null) {
                    tradeInfoDto = tradeService.getByCurrencyAndTradeType(currencyTradeService.getById(e));
                    map.put(e, tradeInfoDto);
                }
                list.add(tradeInfoDto);
            });
            sendMessage(v, list, WebSocketTypeEnum.OPTIONAL_AREA.getCode());
        });
    }

    public List<TradeInfoDto> pushUser(long userId) {
        List<Long> collect = userTradeCollectService.getByUserId(userId);
        List<TradeInfoDto> list = Lists.newArrayList();
        collect.forEach(e -> {
            TradeInfoDto tradeInfoDto = tradeService.getByCurrencyAndTradeType(currencyTradeService.getById(e));
            list.add(tradeInfoDto);
        });
        return list;
    }

    public void sendMessage(Session session, Object obj, int code) {
        if (session.isWritable()) {
            session.sendText(ReturnResponse.builder().code(code).obj(obj).time(System.currentTimeMillis()).build().toJson());
        }
    }

    public void del(long userId) {
        sessionMap.remove(userId);
    }
}
