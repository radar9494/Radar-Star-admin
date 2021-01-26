package com.liuqi.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * Description:自定义shiro-token重写类,用于多类型用户校验
 */
public class MyLoginToken extends UsernamePasswordToken{
    private String loginType;
    public MyLoginToken() {}

    public MyLoginToken(final String username, final String password,
                            final String loginType) {
        super(username, password);
        this.loginType = loginType;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
