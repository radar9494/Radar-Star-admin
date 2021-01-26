package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.ChargeAwardModel;
import com.liuqi.business.model.ChargeAwardModelDto;
import org.apache.ibatis.annotations.Param;


public interface ChargeAwardMapper extends BaseMapper<ChargeAwardModel,ChargeAwardModelDto>{


    int existRecord(@Param("orderId") Long orderId, @Param("recordId") Long recordId);
}
