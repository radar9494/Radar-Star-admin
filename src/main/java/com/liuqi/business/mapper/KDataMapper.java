package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.dto.KDto;
import com.liuqi.business.model.KDataModel;
import com.liuqi.business.model.KDataModelDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


public interface KDataMapper extends BaseMapper<KDataModel,KDataModelDto>{


    List<KDto> queryDataByType(@Param("type") Integer type, @Param("tradeId")  Long tradeId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    KDataModelDto getLastData(@Param("type") Integer type, @Param("tradeId")  Long tradeId);

    KDataModelDto getKByDate(@Param("type")Integer type, @Param("tradeId")Long tradeId, @Param("date")Date date);
}
