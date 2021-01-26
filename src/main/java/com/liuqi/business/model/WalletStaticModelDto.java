package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.util.Date;

@Data
public class WalletStaticModelDto extends WalletStaticModel{

	    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
		private Date date;
		
		private String currencyName;
		@JsonIgnore
	private String sortName="create_time desc,t.id";
	@JsonIgnore
	private String sortType="desc";
}
