package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.dto.TransferTotalDto;
import com.liuqi.business.model.ApiTransferModel;
import com.liuqi.business.model.ApiTransferModelDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;


public interface ApiTransferMapper extends BaseMapper<ApiTransferModel,ApiTransferModelDto>{


    ApiTransferModel getByNameAndNum(@Param("name") String name, @Param("num") String num);

    TransferTotalDto getByUser(@Param("name")String name, @Param("userId")Long userId, @Param("currencyId")Long currencyId,@Param("startDate") Date startDate,@Param("endDate") Date endDate);
}
