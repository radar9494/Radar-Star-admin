package com.liuqi.business.model;

import com.liuqi.business.enums.UsingEnum;
import lombok.Data;


@Data
public class CurrencyAreaModelDto extends CurrencyAreaModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private String statusStr;
	private String currencyName;

	public String getStatusStr() {
		return UsingEnum.getName(super.getStatus());
	}
}
