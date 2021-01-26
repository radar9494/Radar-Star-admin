package com.liuqi.business.model;

import lombok.Data;

@Data
public class UserLevelModelDto extends UserLevelModel{

    private  String userName;
    private  String realName;
    private  String parentName;
    private  String parentName2;
    private String searchTreeInfo;

}
