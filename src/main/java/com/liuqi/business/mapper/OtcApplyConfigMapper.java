package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.OtcApplyConfigModel;
import com.liuqi.business.model.OtcApplyConfigModelDto;
import org.apache.ibatis.annotations.Select;


public interface OtcApplyConfigMapper extends BaseMapper<OtcApplyConfigModel,OtcApplyConfigModelDto>{


    @Select("select *  from t_otc_apply_config where id=1")
    OtcApplyConfigModelDto getConfig();
}
