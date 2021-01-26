package com.liuqi.business.model;

import com.liuqi.business.enums.HelpTypeStatusEnum;
import lombok.Data;

@Data
public class HelpTypeModelDto extends HelpTypeModel{

				
	private String statusStr;

    public String getStatusStr(){
    	return HelpTypeStatusEnum.getName(super.getStatus());
	}

	private String parentName;

	private String sortName="position";
	private String sortType="asc";

}
