package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.util.Date;

@Data
public class RaiseConfigModelDto extends RaiseConfigModel{

	
		
		
		private String currencyName;
		
		
			private Date publishTimeStart;
		private Date publishTimeEnd;
		
		
		
		
		
		
		
		
		
			private Date startTimeStart;
		private Date startTimeEnd;
		
			private Date endTimeStart;
		private Date endTimeEnd;
		@JsonIgnore
	private String sortName="create_time desc,t.id";
	@JsonIgnore
	private String sortType="desc";
}
