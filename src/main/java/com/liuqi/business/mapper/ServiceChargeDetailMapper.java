package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.dto.ChargeDto;
import com.liuqi.business.model.ServiceChargeDetailModel;
import com.liuqi.business.model.ServiceChargeDetailModelDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


public interface ServiceChargeDetailMapper extends BaseMapper<ServiceChargeDetailModel,ServiceChargeDetailModelDto>{


    List<ChargeDto> total(@Param("startTime") Date startTime, @Param("endTime")Date endTime);
}
