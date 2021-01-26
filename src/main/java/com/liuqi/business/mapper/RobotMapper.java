package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.RobotModel;
import com.liuqi.business.model.RobotModelDto;
import org.apache.ibatis.annotations.Param;


public interface RobotMapper extends BaseMapper<RobotModel,RobotModelDto>{


    void removeById(@Param("id") Long id);
}
