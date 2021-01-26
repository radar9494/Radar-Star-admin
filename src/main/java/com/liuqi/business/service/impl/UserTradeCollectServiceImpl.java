package com.liuqi.business.service.impl;


import com.liuqi.business.mapper.UserTradeCollectMapper;
import com.liuqi.business.service.UserTradeCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserTradeCollectServiceImpl implements UserTradeCollectService {

    @Autowired
    private UserTradeCollectMapper userTradeCollectMapper;


    @Override
    public List<Long> getByUserId(Long userId) {
        return userTradeCollectMapper.getByUserId(userId);
    }

    @Override
    public void deleteByUserId(Long userId) {
        userTradeCollectMapper.deleteByUserId(userId);
    }

    @Override
    public void deleteByTradeId(Long tradeId) {
        userTradeCollectMapper.deleteByTradeId(tradeId);
    }

    @Override
    @Transactional
    public void saveCollect(Long userId, Long tradeId) {
        if(!hasCollect(userId,tradeId)) {
            userTradeCollectMapper.saveCollect(userId, tradeId);
        }
    }

    @Override
    public boolean hasCollect(Long userId, Long tradeId) {
        return userTradeCollectMapper.hasCollect(userId,tradeId)>0;
    }

    @Override
    public void cancelCollect(Long userId, Long tradeId) {
        userTradeCollectMapper.cancelCollect(userId,tradeId);
    }
}
