package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liuqi.business.enums.MiningWalletLogEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.mapper.MiningWalletLogMapper;
import lombok.Data;
import java.util.Date;

@Data
public class MiningWalletLogModelDto extends MiningWalletLogModel{



	private String typeStr;

	public String getTypeStr() {
		typeStr= MiningWalletLogEnum.getName(super.getType());
		return typeStr;
	}

	private String userName;

	private String currencyName;



	@JsonIgnore
	private String sortName="create_time desc,t.id";
	@JsonIgnore
	private String sortType="desc";
}
