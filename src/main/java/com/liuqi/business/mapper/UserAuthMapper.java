package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.UserAuthModel;
import com.liuqi.business.model.UserAuthModelDto;


public interface UserAuthMapper extends BaseMapper<UserAuthModel,UserAuthModelDto>{


    UserAuthModelDto getByUserId(Long userId);

    Integer getSuccessIdcart(String idcart);
}
