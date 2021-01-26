package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.LockTransferOutputModel;
import com.liuqi.business.model.LockTransferOutputModelDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;


public interface LockTransferOutputMapper extends BaseMapper<LockTransferOutputModel,LockTransferOutputModelDto>{


    int getTodayTimes(@Param("currencyId") Long currencyId, @Param("userId") Long userId, @Param("startCreateTime")Date startCreateTime,@Param("endCreateTime")Date endCreateTime);
}
