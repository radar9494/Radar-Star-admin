package com.liuqi.business.model;

import com.liuqi.business.enums.WorkResultEnum;
import com.liuqi.business.enums.WorkStatusEnum;
import lombok.Data;

@Data
public class WorkModelDto extends WorkModel{

							
	private String statusStr;

    public String getStatusStr(){
    	return WorkStatusEnum.getName(super.getStatus());
	}

	private String resultStr;

	public String getResultStr() {
		return WorkResultEnum.getName(super.getResult());
	}
	private String typeStr;
    private String userName;
    private String realName;

}
