package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.SuperNodeChargeModel;
import com.liuqi.business.model.SuperNodeChargeModelDto;

import java.util.Date;

public interface SuperNodeChargeService extends BaseService<SuperNodeChargeModel,SuperNodeChargeModelDto>{

    /**
     * 获取日期的手续费
     * @param date
     * @return
     */
    SuperNodeChargeModel getByDate(Date date);

    /**
     * 统计手续费
     * @param date
     */
    void totalCharge(Date date);
}
