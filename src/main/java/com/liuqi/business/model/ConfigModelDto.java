package com.liuqi.business.model;

import com.liuqi.business.enums.ConfigTypeEnum;
import lombok.Data;


@Data
public class ConfigModelDto extends ConfigModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	private String typeStr;


	public String getTypeStr() {
		return ConfigTypeEnum.getName(super.getType());
	}
}
