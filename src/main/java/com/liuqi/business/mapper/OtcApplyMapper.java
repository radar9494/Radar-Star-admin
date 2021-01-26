package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.OtcApplyModel;
import com.liuqi.business.model.OtcApplyModelDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


public interface OtcApplyMapper extends BaseMapper<OtcApplyModel,OtcApplyModelDto>{

   @Select("select * from t_otc_apply where user_id=#{userId} and type=#{type}  order by create_time desc limit 1")
    OtcApplyModel getByUserId(@Param("userId") Long userId, @Param("type")Integer type);
}
