package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.ChargeDto;
import com.liuqi.business.model.ServiceChargeDetailModel;
import com.liuqi.business.model.ServiceChargeDetailModelDto;

import java.util.Date;
import java.util.List;

public interface ServiceChargeDetailService extends BaseService<ServiceChargeDetailModel,ServiceChargeDetailModelDto>{

    /**
     * 汇总
     * @return
     */
    List<ChargeDto> total(Date startTime,Date endTime);
}
