package com.liuqi.business.model;

import com.liuqi.business.enums.HelpStatusEnum;
import lombok.Data;

@Data
public class HelpModelDto extends HelpModel{

				
	private String statusStr;

    public String getStatusStr(){
    	return HelpStatusEnum.getName(super.getStatus());
	}

	private String typeName;

    private String titleLike;

}
