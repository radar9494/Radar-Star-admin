package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.util.Date;

@Data
public class RaiseRecordModelDto extends RaiseRecordModel{

	
		
		private String currencyName;

		private String userName;

		private String exchangeCurrencyName;
		
		
		
		
		@JsonIgnore
	private String sortName="create_time desc,t.id";
	@JsonIgnore
	private String sortType="desc";
}
