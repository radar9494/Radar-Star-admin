package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.CoordinateDto;
import com.liuqi.business.model.MiningIncomeLogModel;
import com.liuqi.business.model.MiningIncomeLogModelDto;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public interface MiningIncomeLogService extends BaseService<MiningIncomeLogModel,MiningIncomeLogModelDto>{

    MiningIncomeLogModel addLog(long userId, BigDecimal num, long currencyId, String currencyName, byte type);

    List<CoordinateDto> sevenDay(long userId, byte type);

    List<MiningIncomeLogModel> findSumByUserId(long userId,Integer type);


    BigDecimal getTotal(long userId);

    BigDecimal getTotalByType(long userId, int i);

    BigDecimal getTotalByType(long userId, int i,Long currencyId);

    BigDecimal yesteartDayTotal(long userId, Long currencyId);
}
