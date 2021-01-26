package com.liuqi.business.model;

import com.liuqi.business.enums.WorkDetailTypeEnum;
import lombok.Data;

@Data
public class WorkDetailModelDto extends WorkDetailModel {

		
	private String typeStr;

    public String getTypeStr(){
    	return WorkDetailTypeEnum.getName(super.getType());
	}
				


}
