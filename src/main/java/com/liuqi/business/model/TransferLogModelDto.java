package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.util.Date;
import com.liuqi.business.enums.TransferLogTypeEnum;

@Data
public class TransferLogModelDto extends TransferLogModel{

	
		
		
		
	private String typeStr;

    public String getTypeStr(){
    	return TransferLogTypeEnum.getName(super.getType());
	}
	
		@JsonIgnore
	private String sortName="create_time desc,t.id";
	@JsonIgnore
	private String sortType="desc";

	private String currencyName;
}
