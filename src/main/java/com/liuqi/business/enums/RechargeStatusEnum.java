package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;

/**
 * //0未处理  1处理成功 2拒绝
STATUS_NO = 0;
STATUS_YES = 1;
TATUS_CANCEL = 2;
 */
public enum RechargeStatusEnum {

	STATUS_NO("未处理", 0),
	STATUS_YES("处理成功", 1),
	TATUS_CANCEL("拒绝", 2);

	private String name;
	private Integer code;

	RechargeStatusEnum(String name, int code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public static String getName(Integer code) {
		if (code != null) {
			for (RechargeStatusEnum e : RechargeStatusEnum.values()) {
				if (e.getCode().equals(code)) {
					return e.getName();
				}
			}
		}
		return "";
	}

	public static List<SelectDto> getList() {
    	List<SelectDto> list = new ArrayList<SelectDto>();
		for (RechargeStatusEnum e : RechargeStatusEnum.values()) {
			list.add(new SelectDto(e.getCode(), e.getName()));
		}
		return list;
	}

}
