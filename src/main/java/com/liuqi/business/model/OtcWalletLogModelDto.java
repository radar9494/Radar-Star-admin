package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liuqi.business.enums.WalletLogTypeEnum;
import lombok.Data;
import java.util.Date;

@Data
public class OtcWalletLogModelDto extends OtcWalletLogModel{

	
		private String userName;

		private String currencyName;

		private String typeStr;

	public String getTypeStr() {
		typeStr=WalletLogTypeEnum.getName(super.getType());
		return typeStr;
	}

	@JsonIgnore
	private String sortName="create_time desc,t.id";
	@JsonIgnore
	private String sortType="desc";
}
