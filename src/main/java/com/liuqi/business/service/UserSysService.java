package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.UserSysModel;
import com.liuqi.business.model.UserSysModelDto;

import javax.servlet.http.HttpServletRequest;

public interface UserSysService extends BaseService<UserSysModel, UserSysModelDto> {

    /**
     * 修改密码
     *
     * @param sysId
     * @param newPwd
     */
    void modifyPwd(Long sysId, String newPwd);

    /**
     * 登录
     *
     * @param name
     * @param pwd
     */
    void login(String name, String pwd, HttpServletRequest request);

    /**
     * 根据名称获取用户
     *
     * @param username
     * @return
     */
    UserSysModelDto findByName(String username);
}
