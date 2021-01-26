package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.FinancingIntroduceModel;
import com.liuqi.business.model.FinancingIntroduceModelDto;


public interface FinancingIntroduceMapper extends BaseMapper<FinancingIntroduceModel,FinancingIntroduceModelDto>{


    FinancingIntroduceModelDto getByConfigId(Long configId);
}
