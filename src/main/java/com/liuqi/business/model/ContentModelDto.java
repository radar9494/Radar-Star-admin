package com.liuqi.business.model;

import com.liuqi.business.enums.ShowEnum;
import lombok.Data;


@Data
public class ContentModelDto extends ContentModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	private String statusStr;

	public String getStatusStr() {
		return ShowEnum.getName(super.getStatus());
	}
}
