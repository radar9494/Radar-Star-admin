package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.util.Date;
import com.liuqi.business.enums.PledgeWalletLogTypeEnum;

@Data
public class PledgeWalletLogModelDto extends PledgeWalletLogModel{

	
		
	private String typeStr;

    public String getTypeStr(){
    	return PledgeWalletLogTypeEnum.getName(super.getType());
	}
	
		 private String userName;
		
		@JsonIgnore
	private String sortName="create_time desc,t.id";
	@JsonIgnore
	private String sortType="desc";
}
