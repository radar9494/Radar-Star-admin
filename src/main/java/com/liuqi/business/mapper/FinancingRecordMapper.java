package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.FinancingRecordModel;
import com.liuqi.business.model.FinancingRecordModelDto;

import java.math.BigDecimal;


public interface FinancingRecordMapper extends BaseMapper<FinancingRecordModel,FinancingRecordModelDto>{


    BigDecimal getConfigQuantity(Long configId);
}
