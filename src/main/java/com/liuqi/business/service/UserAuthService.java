package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.UserAuthModel;
import com.liuqi.business.model.UserAuthModelDto;

public interface UserAuthService extends BaseService<UserAuthModel,UserAuthModelDto>{

    /**
     * 获取用户认证信息
     * @param userId
     * @return
     */
    UserAuthModelDto getByUserId(Long userId);
    String getNameByUserId(Long userId);
    /**
     * 用户是否认证
     * @param userId
     * @return
     */
    boolean auth(Long userId);

    /**
     * 初始化认证信息
     * @param userId
     */
    void initAuth(Long userId);

    /**
     * 认证用户
     * @param userId
     * @param status
     * @param remark
     * @param adminId
     */
    void authUser(Long userId, Integer status,String remark,Long adminId);

    /**
     * 身份证认证成功的数量
     * @param idcart
     * @return
     */
    Integer getSuccessIdcart(String idcart);

    /**
     * 认证第一步
     * @param userId
     * @param userAuthModel
     */
    void approveOne(Long userId, UserAuthModel userAuthModel);
    /**
     * 认证第二步
     * @param auth
     */
    void approveTwo(UserAuthModel auth);

    void init(Long id);
}
