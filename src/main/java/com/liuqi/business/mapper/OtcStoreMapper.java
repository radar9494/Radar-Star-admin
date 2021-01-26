package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.OtcStoreModel;
import com.liuqi.business.model.OtcStoreModelDto;
import org.apache.ibatis.annotations.Param;


public interface OtcStoreMapper extends BaseMapper<OtcStoreModel,OtcStoreModelDto> {


    OtcStoreModelDto getByUserId(@Param("userId") Long userId, @Param("currencyId") Long currencyId);
}
