package com.liuqi.third.google;


/**
 * 谷歌验证
 */
public interface GoogleAuthService {

    String getUserKey();

    /**
     * 验证
     * @param secret  用户私钥
     * @param code 验证码
     * @return
     */
    boolean verify(String secret, long code);
}
