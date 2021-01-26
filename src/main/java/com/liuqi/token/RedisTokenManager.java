package com.liuqi.token;

import com.liuqi.base.BaseConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.model.UserModel;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisTokenManager implements TokenManager {


    @Autowired
    private RedisRepository redisRepository;

    public String getBaseKey(){
        return KeyConstant.KEY_TOKEN+"_";
    }

    /**
     * 获取用户token  10分钟
     * @param user
     * @return
     */
    @Override
    public String getToken(UserModel user) {
        //判断用户是否有token
        String token=this.getUserTokenByUserId(user.getId());
        //存在token的  退出之前的
        if(StringUtils.isNotEmpty(token)){
            this.loginOff(token);
        }
        //获取新的token
        token= MD5Util.MD5Encode(user.getId()+"_"+System.currentTimeMillis());
        String key= getBaseKey()+token;
        while (redisRepository.hasKey(key)) {
            token= MD5Util.MD5Encode(user.getId()+"_"+System.currentTimeMillis());
            key= getBaseKey()+token;
        }
        //token
        redisRepository.hset(key, "user", user, BaseConstant.TOKEN_SESSION_TIME, TimeUnit.MINUTES);
        //记录 userId对应token  不过期
        redisRepository.set(getBaseKey()+ user.getId(),token);
        return token;
    }

    @Override
    public void refreshUserToken(String token) {
        String key= getBaseKey()+token;
        if (redisRepository.hasKey(key)) {
            redisRepository.expire(key, BaseConstant.TOKEN_SESSION_TIME, TimeUnit.MINUTES);
        }
    }

    @Override
    public void loginOff(String token) {
        Long userId=this.getUserIdByToken(token);
        String key= getBaseKey()+token;
        redisRepository.del(key);
        //删除对应信息
        if(userId>0) {
            redisRepository.del(getBaseKey() + userId);
        }
    }


    /**
     * 设置值到session中
     * @param token
     * @param paramKey
     * @param paramValue
     * @return
     */
    public boolean setAttribute(String token, String paramKey,Object paramValue){
        String key= getBaseKey()+token;
        return redisRepository.hset(key, paramKey, paramValue, BaseConstant.TOKEN_SESSION_TIME, TimeUnit.MINUTES);
    }

    /**
     * 在session中获取值
     * @param token
     * @param paramKey
     * @return
     */
    public Object getAttribute(String token, String paramKey){
        String key= getBaseKey()+token;
        if (redisRepository.hasKey(key)) {
            return redisRepository.hget(key, paramKey);
        }
        return null;
    }
    @Override
    public Long getUserIdByToken(String token){
        UserModel user = this.getUserByToken(token);
        return user != null ? user.getId() : -1;
    }

    @Override
    public UserModel getUserByToken(String token) {
        String key= getBaseKey()+token;
        if (redisRepository.hasKey(key)) {
            Object obj = redisRepository.hget(key, "user");
            if(obj!=null){
                return (UserModel) obj;
            }
        }
        return null;
    }

    @Override
    public String getUserTokenByUserId(Long userId) {
        String token= redisRepository.getString(getBaseKey()+ userId);
        return StringUtils.isNotEmpty(token)?token:"";
    }



    @Override
    public String getToken(UserModel user,Integer time) {
        //判断用户是否有token
        String token=this.getUserTokenByUserId(user.getId());
        //存在token的  退出之前的
        if(StringUtils.isNotEmpty(token)){
            this.loginOff(token);
        }
        //获取新的token
        token= MD5Util.MD5Encode(user.getId()+"_"+System.currentTimeMillis());
        String key= getBaseKey()+token;
        while (redisRepository.hasKey(key)) {
            token= MD5Util.MD5Encode(user.getId()+"_"+System.currentTimeMillis());
            key= getBaseKey()+token;
        }
        //token
        redisRepository.hset(key, "user", user, time, TimeUnit.DAYS);
        //记录 userId对应token  不过期
        redisRepository.set(getBaseKey()+ user.getId(),token);
        return token;
    }
}
