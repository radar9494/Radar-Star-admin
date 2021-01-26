package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.util.Date;

@Data
public class TransferConfigModelDto extends TransferConfigModel{

	
		private String currencyName;
		
		
		@JsonIgnore
	private String sortName="create_time desc,t.id";
	@JsonIgnore
	private String sortType="desc";
}
