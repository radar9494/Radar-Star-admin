package com.liuqi.business.model;

import lombok.Data;
import com.liuqi.business.enums.UserPayPayTypeEnum;
import com.liuqi.business.enums.UserPayStatusEnum;

@Data
public class UserPayModelDto extends UserPayModel{

		
	private String payTypeStr;

    public String getPayTypeStr(){
    	return UserPayPayTypeEnum.getName(super.getPayType());
	}
							
	private String statusStr;

    public String getStatusStr(){
    	return UserPayStatusEnum.getName(super.getStatus());
	}

	private  String userName;
	private  String realName;

}
