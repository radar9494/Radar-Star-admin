package com.liuqi.business.model;

import com.liuqi.business.enums.*;
import lombok.Data;


@Data
public class RechargeModelDto extends RechargeModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private String statusStr;

	public String getStatusStr() {
		return RechargeStatusEnum.getName(super.getStatus());
	}

	private String userName;
	private String realName;
	private String currencyName;

	//类型
	private String typeStr;

	public String getTypeStr() {
		return InnerOuterEnum.getName(super.getType());
	}

	//推送类型
	private String sendTypeStr;

	public String getSendTypeStr() {
		return RechargeSendTypeEnum.getName(super.getSendType());
	}

	private String protocolStr;
	public String getProtocolStr() {
		return ProtocolEnum.getName(super.getProtocol());
	}
	private String walletTypeStr;
	public String getWalletTypeStr() { return WalletTypeEnum.getName( super.getWalletType());}

}
