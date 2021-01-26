package com.liuqi.business.service;


import java.util.List;

public interface UserTradeCollectService {

    boolean hasCollect(Long userId, Long tradeId);

    List<Long> getByUserId(Long userId);

    void deleteByUserId(Long userId);

    void deleteByTradeId(Long tradeId);

    void saveCollect(Long userId, Long tradeId);

    void cancelCollect(Long userId, Long tradeId);
}
