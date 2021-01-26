package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.UserAdminModel;
import com.liuqi.business.model.UserAdminModelDto;

import javax.servlet.http.HttpServletRequest;

public interface UserAdminService extends BaseService<UserAdminModel,UserAdminModelDto>{


    /**
     * 修改密码
     * @param adminId
     * @param newPwd
     */
    void modifyPwd(Long adminId, String newPwd);

    void modifyPwdByOld(Long adminId, String old, String pwd);

    /**
     * 登录
     * @param name
     * @param pwd
     */
    void login(String name, String pwd,Long code,HttpServletRequest request);

    /**
     * 根据名称获取用户
     * @param username
     * @return
     */
    UserAdminModelDto findByName(String username);

    /**
     * 获取名字
     * @param id
     * @return
     */
    String getNameById(Long id);
}
