package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.UserAdminModel;
import com.liuqi.business.model.UserAdminModelDto;


public interface UserAdminMapper extends BaseMapper<UserAdminModel,UserAdminModelDto>{


    UserAdminModelDto findByName(String name);

}
