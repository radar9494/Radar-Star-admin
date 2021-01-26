package com.liuqi.business.model;

import lombok.Data;

@Data
public class LockTransferInputModelDto extends LockTransferInputModel{


    private String userName;
    private String realName;
    private String currencyName;


}
