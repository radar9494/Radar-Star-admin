package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.CurrencyModel;
import com.liuqi.business.model.CurrencyModelDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CurrencyMapper extends BaseMapper<CurrencyModel,CurrencyModelDto>{


    CurrencyModelDto getByName(String name);

    List<Long> getLikeByName(@Param("currencyName") String currencyName, @Param("status")Integer status);
}
