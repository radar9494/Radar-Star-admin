package com.liuqi.business.model;

import com.liuqi.base.BaseConstant;
import com.liuqi.business.enums.UserSysStatusEnum;
import lombok.Data;
import org.crazycake.shiro.AuthCachePrincipal;

@Data
public class UserSysModelDto extends UserSysModel implements AuthCachePrincipal{

    private String currencyName;


    private String statusStr;

    public String getStatusStr() {
        return UserSysStatusEnum.getName(super.getStatus());
    }

    @Override
    public String getAuthCacheKey() {
        return getId()+"";
    }
}
