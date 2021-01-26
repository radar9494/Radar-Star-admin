package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.OtcApplyModel;
import com.liuqi.business.model.OtcApplyModelDto;

public interface OtcApplyService extends BaseService<OtcApplyModel,OtcApplyModelDto>{


    void apply(Long userId);

    OtcApplyModel getByUserId(Long userId,Integer type);

    void audit(Long id, Long adminId);

    void pledgeCancel(Long userId);

    Integer getOtcApplyStatus(Long userId);

    void refuse(Long id, Long adminId);
}
