package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.TradeRecordModel;
import com.liuqi.business.model.TradeRecordUserModel;
import com.liuqi.business.model.TradeRecordUserModelDto;

import java.util.List;

public interface TradeRecordUserService extends BaseService<TradeRecordUserModel,TradeRecordUserModelDto>{

    List<TradeRecordUserModelDto> findUserRecord(Long userId, Long tradeId, boolean limit, Integer count);

    /**
     * 生成用户记录信息
     * @param tradeRecordModel
     */
    void insertUserRecord(TradeRecordModel tradeRecordModel);
}
