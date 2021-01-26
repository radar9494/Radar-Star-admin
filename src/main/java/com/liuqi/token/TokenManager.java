package com.liuqi.token;

import com.liuqi.business.model.UserModel;

public interface TokenManager {

    /**
     * 创建用户token
     * @param user
     * @return
     */
    String getToken(UserModel user);

    /**
     * 刷新用户
     * @param token
     * @return
     */
    void refreshUserToken(String token);
    /**
     * 用户退出登录
     * @param token
     */
    void loginOff(String token);

    /**
     * 获取用户信息
     * @param token
     * @return
     */
    Long getUserIdByToken(String token);

    /**
     * 获取用户信息
     * @param token
     * @return
     */
    UserModel getUserByToken(String token);

    /**
     * 获取用户utoken
     * @return
     */
    String getUserTokenByUserId(Long userId);

    String getToken(UserModel user,Integer time);
}
