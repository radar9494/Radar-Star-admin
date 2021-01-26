package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liuqi.business.enums.UsingEnum;
import com.liuqi.utils.SignUtil;
import lombok.Data;
import java.util.Date;

@Data
public class UserApiKeyModelDto extends UserApiKeyModel{

	private String userName;

	private String statusStr;

	private String currencyName;

	public String getStatusStr() {
		return UsingEnum.getName(super.getStatus());
	}

	@JsonIgnore
	private String sortName="create_time desc,t.id";
	@JsonIgnore
	private String sortType="desc";

	private String decodeSecretKey;

	public String getDecodeSecretKey() {
		if(super.getSecretKey()!=null){
			return SignUtil.getDecode(super.getSecretKey());
		}
		return decodeSecretKey;
	}

}
