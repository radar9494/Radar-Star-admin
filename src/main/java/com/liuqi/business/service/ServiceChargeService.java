package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.ServiceChargeModel;
import com.liuqi.business.model.ServiceChargeModelDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ServiceChargeService extends BaseService<ServiceChargeModel,ServiceChargeModelDto>{

    /**
     * 汇总
     */
    void totalCharge(Date date);

    /**
     * 获取指定日期所有币种手续费
     * @param date
     * @return
     */
    List<ServiceChargeModelDto> getByDate(Date date);

    /**
     * 获取指定日期指定币种手续费
     * @param date
     * @param currencyId
     * @return
     */
    ServiceChargeModelDto getByDateAndCurrency(Date date,Long currencyId);

    /**
     * 获取日期的总手续费
     * @param date
     * @return
     */
    BigDecimal getTotalByDate(Date date);
}
