package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.CtcOrderLogModel;
import com.liuqi.business.model.CtcOrderLogModelDto;
import com.liuqi.business.model.CtcOrderModel;

import java.util.List;

public interface CtcOrderLogService extends BaseService<CtcOrderLogModel,CtcOrderLogModelDto>{

    void addLog(Long orderId,String opeName,String remark);

    List<CtcOrderLogModelDto> getByOrderId(Long orderId);
}
