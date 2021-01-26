package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PledgeWalletModelDto extends PledgeWalletModel{

	
		
		private Long currencyId;
		private String currencyName;
		@JsonIgnore
	private String sortName="create_time desc,t.id";
	@JsonIgnore
	private String sortType="desc";

	private BigDecimal total;

	private BigDecimal cnyQuantity;

	private String userName;
}
