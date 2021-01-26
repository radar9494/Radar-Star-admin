package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.util.Date;

@Data
public class OtcApplyConfigModelDto extends OtcApplyConfigModel{

	
		  private String rateCurrencyName;

		  private Long currencyId; //用于统计
		
		@JsonIgnore
	private String sortName="create_time desc,t.id";
	@JsonIgnore
	private String sortType="desc";
}
