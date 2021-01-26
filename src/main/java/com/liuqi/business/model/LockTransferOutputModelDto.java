package com.liuqi.business.model;

import lombok.Data;

@Data
public class LockTransferOutputModelDto extends LockTransferOutputModel{

    private String userName;
    private String realName;
    private String currencyName;
    private String receiveUserName;
    private String receiveRealName;


}
