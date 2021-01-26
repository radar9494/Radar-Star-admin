package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.RaiseRecordModel;
import com.liuqi.business.model.RaiseRecordModelDto;

import java.math.BigDecimal;
import java.util.List;

public interface RaiseRecordService extends BaseService<RaiseRecordModel,RaiseRecordModelDto>{


    List<RaiseRecordModel> getByConfigId(Long id);

    void buy(Long userId, BigDecimal quantity,Long configId,Integer type);
}
