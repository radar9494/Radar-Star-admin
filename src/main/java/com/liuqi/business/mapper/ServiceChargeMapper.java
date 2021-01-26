package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.ServiceChargeModel;
import com.liuqi.business.model.ServiceChargeModelDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


public interface ServiceChargeMapper extends BaseMapper<ServiceChargeModel,ServiceChargeModelDto>{


    List<ServiceChargeModelDto> getByDate(@Param("date") Date date);

    ServiceChargeModelDto getByDateAndCurrency(@Param("date") Date date, @Param("currencyId") Long currencyId);
}
