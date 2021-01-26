package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.RaiseRecordModel;
import com.liuqi.business.model.RaiseRecordModelDto;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface RaiseRecordMapper extends BaseMapper<RaiseRecordModel,RaiseRecordModelDto>{


    @Select("select  *  from t_raise_record where config_id=#{id}")
    List<RaiseRecordModel> getByConfigId(Long id);
}
