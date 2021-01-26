package com.liuqi.base;


import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.model.RoleModel;
import com.liuqi.business.model.UserAdminModel;
import com.liuqi.business.model.UserAdminModelDto;
import com.liuqi.exception.BusinessException;
import org.apache.shiro.SecurityUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 后期可以改为从其他地方获取
 */
public class LoginAdminUserHelper {

    /**
     * 获取当前用户id
     *
     * @return
     */
    public static Long getAdminId() {
       Long userId= (Long) SecurityUtils.getSubject().getSession().getAttribute(BaseConstant.ADMIN_USERID_SESSION);
          if(userId==null||userId==0){
              throw new BusinessException("登录异常!");
          }
        return userId;
    }

    /**
     * 获取当前用户
     *
     * @return
     */
    public static UserAdminModelDto getAdmin() {
        return JSONObject.parseObject(SecurityUtils.getSubject().getSession().getAttribute(BaseConstant.ADMIN_USER_SESSION).toString(),UserAdminModelDto.class);
    }
    /**
     * 获取当前用户角色
     *
     * @return
     */
    public static RoleModel getRole() {
        return JSONObject.parseObject(SecurityUtils.getSubject().getSession().getAttribute(BaseConstant.ADMIN_USER_ROLE).toString(),RoleModel.class);
    }
}
