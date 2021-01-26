package com.liuqi.base;

import com.liuqi.business.model.UserModel;
import com.liuqi.exception.NoLoginException;

import javax.servlet.http.HttpServletRequest;


public abstract class BaseFrontController extends BaseController {


    /**
     * 获取登录用户id
     *
     * @param request
     * @return
     * @throws NoLoginException
     */
    public Long getUserId(HttpServletRequest request) throws NoLoginException {
        return LoginUserTokenHelper.getUserId(request);
    }

    /**
     * 获取登录用户信息
     *
     * @param request
     * @return
     * @throws NoLoginException
     */
    public UserModel getUser(HttpServletRequest request) throws NoLoginException {
        return LoginUserTokenHelper.getUser(request);
    }
}
