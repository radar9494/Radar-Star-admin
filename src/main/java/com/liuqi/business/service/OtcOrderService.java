package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.OtcOrderModel;
import com.liuqi.business.model.OtcOrderModelDto;

import java.math.BigDecimal;

public interface OtcOrderService extends BaseService<OtcOrderModel,OtcOrderModelDto>{

    /**
     * 发布
     * @param order
     */
    void publish(OtcOrderModel order);

    /**
     * 交易
     * @param userId
     * @param orderId
     * @param quantity
     * @return
     */
    Long trade(Long userId, Long orderId, BigDecimal quantity);

    /**
     * 取消
     * @param orderId
     * @param userId   当前用户
     * @param checkUser 是否检查用户=userId
     */
    void cancel(Long orderId,Long userId,boolean checkUser);

    /**
     * 修改上下架
     * @param orderId
     * @param cancelStatus
     */
    void cancelStatus(Long orderId,Integer cancelStatus);
}
