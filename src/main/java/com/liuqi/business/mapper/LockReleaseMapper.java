package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.LockReleaseModel;
import com.liuqi.business.model.LockReleaseModelDto;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;


public interface LockReleaseMapper extends BaseMapper<LockReleaseModel,LockReleaseModelDto>{


    BigDecimal getTodayQuantityByDate(@Param("userId") Long userId,@Param("orderId")  Long orderId, @Param("tradeType") Integer tradeType, @Param("date") Date date);

    LockReleaseModelDto getByDate(@Param("userId")Long userId, @Param("currencyId")Long currencyId,  @Param("tradeType")Integer tradeType,@Param("date") Date date);
}
