package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.UserAdminLoginModel;
import com.liuqi.business.model.UserAdminLoginModelDto;

public interface UserAdminLoginService extends BaseService<UserAdminLoginModel, UserAdminLoginModelDto> {


    void addLog(String loginName, String ip, String city, String remark);

}
