package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.CtcConfigModelDto;
import com.liuqi.business.model.CtcOrderModel;
import com.liuqi.business.model.CtcOrderModelDto;

import java.math.BigDecimal;

public interface CtcOrderService extends BaseService<CtcOrderModel,CtcOrderModelDto>{

    /**
     * 创建订单
     * @param userId
     * @param config
     * @param price
     * @param quantity
     * @param tradeType
     * @return
     */
    Long createOrder(Long userId, CtcConfigModelDto config, BigDecimal price, BigDecimal quantity, Integer tradeType,String opeName);

    /**
     * 匹配商家
     * @param orderId
     */
    void matchStore(Long orderId);

    /**
     * 完成
     * @param orderId
     * @param opeName
     */
    void confirm(Long orderId,String remark, String opeName);

    /**
     * 取消
     *
     * @param orderId
     * @param opeName
     */
    void cancel(Long orderId,String remark,String opeName);

    /**
     * 自动取消
     *
     * @param orderId
     */
    void autoCancel(Long orderId);


}
