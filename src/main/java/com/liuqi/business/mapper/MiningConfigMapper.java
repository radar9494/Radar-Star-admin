package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.MiningConfigModel;
import com.liuqi.business.model.MiningConfigModelDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


public interface MiningConfigMapper extends BaseMapper<MiningConfigModel,MiningConfigModelDto>{

    @Select("select * from t_mining_config  where type=#{type} and currency_id=#{currencyId} ")
    MiningConfigModel findOne(@Param("type") Integer type,@Param("currencyId") Long currencyId);
}
