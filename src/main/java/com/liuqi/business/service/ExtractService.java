package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.CurrencyCountDto;
import com.liuqi.business.dto.chain.ExtractSearchDto;
import com.liuqi.business.model.CurrencyConfigModel;
import com.liuqi.business.model.ExtractModel;
import com.liuqi.business.model.ExtractModelDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ExtractService extends BaseService<ExtractModel,ExtractModelDto>{



    /**
     * 申请订单
     * @param extractModel
     */
    void extractApply(ExtractModel extractModel, CurrencyConfigModel config);

    /**
     * 手动确认
     * @param orderId 订单
     * @param reason 理由
     * @param hash  hash
     * @param adminUserId
     */
    void confirmOrder (Long orderId,String reason,String hash, Long adminUserId);

    /**
     * 拒绝
     * @param orderId
     * @param reason
     * @param adminUserId
     */
    void refuseOrder(Long orderId,String reason, Long adminUserId);

    /**
     * 接口订单
     * @param orderId
     * @param adminId
     */
    void autoExtract(Long orderId, Long adminId);
    /**
     * 处理成功
     */
    void doSuccess(ExtractModel extractModel, ExtractSearchDto dto);

    /**
     * 按照日期查询各币种笔数和数量
     *
     * @param date
     * @return
     */
    List<CurrencyCountDto> queryCountByDate(Date date,Long currencyId);

    /**
     * 提现系统打回
     * @param model
     * @param dto
     */
    void doWait(ExtractModel model, ExtractSearchDto dto);

    BigDecimal getTotal(ExtractModelDto rechargeModelDto);
}
