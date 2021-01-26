package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.FinancingIntroduceModel;
import com.liuqi.business.model.FinancingIntroduceModelDto;

public interface FinancingIntroduceService extends BaseService<FinancingIntroduceModel,FinancingIntroduceModelDto>{


    FinancingIntroduceModelDto getByConfigId(Long configId);

    void init(Long configId);
}
