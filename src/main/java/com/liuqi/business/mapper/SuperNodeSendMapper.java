package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.SuperNodeSendModel;
import com.liuqi.business.model.SuperNodeSendModelDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;


public interface SuperNodeSendMapper extends BaseMapper<SuperNodeSendModel,SuperNodeSendModelDto>{


    SuperNodeSendModelDto getByUserIdAndDate(@Param("userId") Long userId, @Param("date") Date date);
}
