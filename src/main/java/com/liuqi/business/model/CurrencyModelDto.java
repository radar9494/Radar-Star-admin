package com.liuqi.business.model;

import com.liuqi.business.enums.ProtocolEnum;
import com.liuqi.business.enums.UsingEnum;
import lombok.Data;

import java.util.List;


@Data
public class CurrencyModelDto extends CurrencyModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private String sortName="status asc,position";
	private String sortType="asc";

	private String statusStr;

	public String getStatusStr() {
		return  UsingEnum.getName(super.getStatus());
	}

	private String protocolStr;

	public String getProtocolStr() {
		return ProtocolEnum.getName(super.getProtocol());
	}

	private String protocol2Str;

	public String getProtocol2Str() {
		return ProtocolEnum.getName(super.getProtocol2());
	}

	private Integer curBlock;


}
