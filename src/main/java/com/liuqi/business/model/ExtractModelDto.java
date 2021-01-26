package com.liuqi.business.model;

import com.liuqi.business.enums.*;
import lombok.Data;


@Data
public class ExtractModelDto extends ExtractModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private String statusStr;

	public String getStatusStr() {
		return ExtractMoneyEnum.getName(super.getStatus());
	}

	private String userName;
	private String realName;
	private String currencyName;
	private String rateCurrencyName;

	private String idStr;

	public String getIdStr() {
		return super.getId()+"";
	}

	//类型
	private String typeStr;
	public String getTypeStr() {
		return InnerOuterEnum.getName(super.getType());
	}
	//接受用户
	private String receiveUserName;
	//处理用户
	private String dealAdminName;

	private String protocolStr;
	public String getProtocolStr() {
		return ProtocolEnum.getName(super.getProtocol());
	}
	private String walletTypeStr;
	public String getWalletTypeStr() { return WalletTypeEnum.getName( super.getWalletType());}

}
