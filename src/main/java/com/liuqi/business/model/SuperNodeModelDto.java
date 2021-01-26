package com.liuqi.business.model;

import lombok.Data;

@Data
public class SuperNodeModelDto extends SuperNodeModel{


    private String userName;
    private String realName;
    private String recommendUserName;
    private String recommendRealName;
    private String  currencyName;

}
