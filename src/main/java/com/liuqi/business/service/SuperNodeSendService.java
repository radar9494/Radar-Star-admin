package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.SuperNodeSendModel;
import com.liuqi.business.model.SuperNodeSendModelDto;

import java.math.BigDecimal;
import java.util.Date;

public interface SuperNodeSendService extends BaseService<SuperNodeSendModel,SuperNodeSendModelDto>{

    SuperNodeSendModelDto getByUserIdAndDate(Long userId,Date date);

    void createOrder(Long userId, Long currencyId, BigDecimal quantity, Date date);
    /**
     * 生成分红单据
     * @param date
     */
    void createChargeOrder(Date date);

    /**
     * 分红到钱包
     */
    void realse();

    /**
     * 订单释放到钱包
     * @param id
     */
    void recordRelease(Long id);
}
