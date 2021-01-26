package com.liuqi.business.model;

import com.liuqi.business.enums.UserAuthEnum;
import lombok.Data;

@Data
public class UserAuthModelDto extends UserAuthModel{


    private  String userName;
    private String authStatusStr;

    public String getAuthStatusStr() {
        return UserAuthEnum.getName(super.getAuthStatus());
    }
}
