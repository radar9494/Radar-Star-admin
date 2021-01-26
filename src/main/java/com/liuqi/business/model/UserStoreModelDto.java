package com.liuqi.business.model;

import lombok.Data;
import com.liuqi.business.enums.UserStoreStatusEnum;

@Data
public class UserStoreModelDto extends UserStoreModel{

		
	private String statusStr;

    public String getStatusStr(){
    	return UserStoreStatusEnum.getName(super.getStatus());
	}
					

	private String userName;
	private String realName;
}
