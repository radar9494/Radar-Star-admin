package com.liuqi.business.model;

import com.liuqi.business.enums.WalletDoEnum;
import lombok.Data;

import java.util.Date;

@Data
public class SuperNodeSendModelDto extends SuperNodeSendModel{

    private String statusStr;

    public String getStatusStr() {
        return WalletDoEnum.getName(super.getStatus());
    }

    private Date sendDateStart;
    private Date  sendDateEnd;

    private String userName;
    private String realName;
    private String  currencyName;


}
