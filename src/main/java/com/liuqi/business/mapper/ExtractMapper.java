package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.dto.CurrencyCountDto;
import com.liuqi.business.model.ExtractModel;
import com.liuqi.business.model.ExtractModelDto;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public interface ExtractMapper extends BaseMapper<ExtractModel,ExtractModelDto>{


    List<CurrencyCountDto> queryCountByDate(@Param("date") Date date,@Param("currencyId") Long currencyId,@Param("status") Integer status);

    BigDecimal getTotal(ExtractModelDto rechargeModelDto);
}
