package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.ChargeAwardModel;
import com.liuqi.business.model.ChargeAwardModelDto;

public interface ChargeAwardService extends BaseService<ChargeAwardModel,ChargeAwardModelDto>{

    /**
     * 根据成交信息生成记录
     * @param recordId
     * @return
     */
    void createRecord(Long recordId);

    /**
     * 是否存在记录
     * @param orderId
     * @param recordId
     * @return
     */
    boolean existRecord(Long orderId,Long recordId);

    /**
     * 锁仓释放
     * @param id
     */
    void recordRelease(Long id);
}
