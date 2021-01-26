package com.liuqi.shiro;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.base.BaseConstant;
import com.liuqi.business.enums.UserAdminStatusEnum;
import com.liuqi.business.model.RoleModel;
import com.liuqi.business.model.UserAdminModelDto;
import com.liuqi.business.service.RolePermissionService;
import com.liuqi.business.service.UserAdminService;
import com.liuqi.utils.MD5Util;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

public class AdminShiroRealm extends AuthorizingRealm{
    @Autowired
    @Lazy
    private UserAdminService userAdminService;
    @Autowired
    @Lazy
    private RolePermissionService rolePermissionService;

    @Override
    public String getName() {
        return BaseConstant.TYPE_ADMIN;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        List<String> realmNameList = new ArrayList<>(principals.getRealmNames());
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        if (realmNameList.contains(BaseConstant.TYPE_ADMIN)) {
            Session session = SecurityUtils.getSubject().getSession();
            //追加管理员角色
            authorizationInfo.addRole(BaseConstant.TYPE_ADMIN);
            RoleModel role = JSONObject.parseObject(session.getAttribute(BaseConstant.ADMIN_USER_ROLE).toString(), RoleModel.class);
            //后台角色
            authorizationInfo.addRole(role.getEnName());

            List<String> permissions = rolePermissionService.getPermissionNameByRole(role.getId());
            if (permissions != null && permissions.size() > 0) {
                for (String p : permissions) {
                    authorizationInfo.addStringPermission(p);
                }
            }

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
        UserAdminModelDto admin = userAdminService.findByName(username);
        if (admin == null) {
            throw new IncorrectCredentialsException();
        }
        if (!UserAdminStatusEnum.USDING.getCode().equals(admin.getStatus())) { //账户冻结
            throw new LockedAccountException();
        }
        ByteSource credentialsSalt = ByteSource.Util.bytes((user.getLoginType()+BaseConstant.BASE_PROJECT).getBytes());
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                admin, //用户名
                admin.getPwd(), //密码
                credentialsSalt,
                getName()  //realm name
        );
        return authenticationInfo;
    }
}
