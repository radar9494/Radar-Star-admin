package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.CurrencyCountDto;
import com.liuqi.business.model.RechargeModel;
import com.liuqi.business.model.RechargeModelDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface RechargeService extends BaseService<RechargeModel,RechargeModelDto>{

    /**
     * 判断是否存在这个hash的记录
     * @param currencyId
     * @param remark
     */
    boolean existRemark(Long currencyId,Long userId,String remark);

    /**
     * 自动到账
     * @param userId
     * @param currencyId
     * @param quantity
     * @param address
     * @param hash
     * @param createTime
     */
    void autoRecharge(Long userId, Long currencyId, BigDecimal quantity, String address, String hash, Date createTime,Integer protocol);


    /**
     *内部充值
     * @param userId
     * @param currencyId
     * @param quantity
     * @param address
     * @param hash
     * @param createTime
     */
    void innerRecharge(Long userId, Long currencyId, BigDecimal quantity, String address, String hash, Date createTime,Integer protocol);
    /**
     * 按照日期查询各币种笔数和数量
     *
     * @param date
     * @return
     */
    List<CurrencyCountDto> queryCountByDate(Date date,Long currencyId);

    BigDecimal getTotal(RechargeModelDto rechargeModelDto);
}
