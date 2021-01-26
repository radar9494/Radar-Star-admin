package com.liuqi.shiro;

import com.liuqi.base.BaseConstant;
import com.liuqi.business.enums.UserSysStatusEnum;
import com.liuqi.business.model.UserSysModelDto;
import com.liuqi.business.service.UserSysService;
import com.liuqi.utils.MD5Util;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

public class SysShiroRealm extends AuthorizingRealm {
    @Autowired
    @Lazy
    private UserSysService userSysService;

    @Override
    public String getName() {
        return BaseConstant.TYPE_SYS;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        List<String> realmNameList = new ArrayList<>(principals.getRealmNames());
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        if (realmNameList.contains(BaseConstant.TYPE_SYS)) {
            //角色
            authorizationInfo.addRole(BaseConstant.TYPE_SYS);
        }
        return authorizationInfo;
    }

    /*主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        MyLoginToken user = (MyLoginToken) token;
        //获取用户的输入的账号.
        String username = user.getUsername();
        UserSysModelDto sys = userSysService.findByName(username);
        if (sys == null) {
            throw new IncorrectCredentialsException();
        }
        if (!UserSysStatusEnum.USDING.getCode().equals(sys.getStatus())) { //账户
            throw new LockedAccountException();
        }
        ByteSource credentialsSalt = ByteSource.Util.bytes((user.getLoginType()+BaseConstant.BASE_PROJECT).getBytes());
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                sys, //用户名
                sys.getPwd(), //密码
                credentialsSalt,
                getName()  //realm name
        );
        return authenticationInfo;
    }
}
