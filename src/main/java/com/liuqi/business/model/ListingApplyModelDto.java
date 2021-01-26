package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.util.Date;
import com.liuqi.business.enums.ListingApplyStatusEnum;

@Data
public class ListingApplyModelDto extends ListingApplyModel{

	
		
		
		
		
		
		
		
		
		
	private String statusStr;

    public String getStatusStr(){
    	return ListingApplyStatusEnum.getName(super.getStatus());
	}
	
		@JsonIgnore
	private String sortName="create_time desc,t.id";
	@JsonIgnore
	private String sortType="desc";
}
