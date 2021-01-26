package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.SuperNodeModel;
import com.liuqi.business.model.SuperNodeModelDto;


public interface SuperNodeMapper extends BaseMapper<SuperNodeModel,SuperNodeModelDto>{


    SuperNodeModelDto getByUserId(Long userId);

    int getTotalCount();


}
