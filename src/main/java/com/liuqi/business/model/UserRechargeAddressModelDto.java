package com.liuqi.business.model;

import com.liuqi.business.enums.ProtocolEnum;
import lombok.Data;


@Data
public class UserRechargeAddressModelDto extends UserRechargeAddressModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	private String userName;
	private String realName;
	private String protocolStr;

	public String getProtocolStr() {
		return ProtocolEnum.getName(super.getProtocol());
	}
}
