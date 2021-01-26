package com.liuqi.base;

import com.liuqi.business.model.UserModel;
import com.liuqi.exception.NoLoginException;
import com.liuqi.spring.SpringUtil;
import com.liuqi.token.RedisTokenManager;

import javax.servlet.http.HttpServletRequest;

public class LoginUserTokenHelper {


    private static RedisTokenManager redisTokenManager;


    public static RedisTokenManager getRedisTokenManager() {
        if(redisTokenManager==null){
            redisTokenManager= (RedisTokenManager) SpringUtil.getBean("redisTokenManager");
        }
        return redisTokenManager;
    }

    public static String getToken(HttpServletRequest request){
        return request.getHeader(BaseConstant.TOKEN_NAME);
    }

    /**获取当前用户id
     *
     * @param request
     * @return
     */
    public static Long getUserId(HttpServletRequest request) throws NoLoginException {
        String token=getToken(request);
        Long userId= getRedisTokenManager().getUserIdByToken(token);
        if(userId!=null){
            return userId;
        }
        throw new NoLoginException("未获取到用户信息"+request.getRequestURL());
    }

    /**获取当前用户id
     *
     * @param token
     * @return
     */
    public static Long getUserIdByToken(String token) throws NoLoginException {
        Long userId= getRedisTokenManager().getUserIdByToken(token);
        if(userId!=null){
            return userId;
        }
        throw new NoLoginException("未获取到用户信息");
    }

    /**获取当前用户id
     *
     * @param token
     * @return
     */
    public static Long getUserIdByToken(String token,HttpServletRequest request) throws NoLoginException {
        return getUserIdByToken(token);
    }

    /**
     * 获取当前用户
     * @param request
     * @return
     */
    public static UserModel getUser(HttpServletRequest request){
        String token=getToken(request);
        return  getRedisTokenManager().getUserByToken(token);
    }

    /**
     * 移除
     * @param request
     */
    public static void removeUser(HttpServletRequest request){
        String token=getToken(request);
        getRedisTokenManager().loginOff(token);
    }
}
