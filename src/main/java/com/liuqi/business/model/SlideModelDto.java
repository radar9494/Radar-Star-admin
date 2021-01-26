package com.liuqi.business.model;

import com.liuqi.business.enums.ShowEnum;
import com.liuqi.business.enums.SlideOutHrefEnum;
import com.liuqi.business.enums.SlideTypeEnum;
import lombok.Data;


@Data
public class SlideModelDto extends SlideModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	private String statusStr;

	public String getStatusStr() {
		return ShowEnum.getName(super.getStatus());
	}
	private String typeStr;
	public String getTypeStr() {
		return SlideTypeEnum.getName(super.getType());
	}

	private String outHrefStr;

	public String getOutHrefStr() {
		return SlideOutHrefEnum.getName(super.getOutHref());
	}
}
