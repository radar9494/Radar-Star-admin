package com.liuqi.business.model;

import com.liuqi.business.enums.WorkTypeStatusEnum;
import lombok.Data;

@Data
public class WorkTypeModelDto extends WorkTypeModel {

		
	private String statusStr;

    public String getStatusStr(){
    	return WorkTypeStatusEnum.getName(super.getStatus());
	}
		


}
