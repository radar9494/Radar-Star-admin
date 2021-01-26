package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;


public enum OtcOrderPayEnum {

	NONSUPPORT("不支持", 0),
	SUPPORT("支持", 1);

	private String name;
	private Integer code;

	OtcOrderPayEnum(String name, int code) {
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
			for (OtcOrderPayEnum e : OtcOrderPayEnum.values()) {
				if (e.getCode().equals(code)) {
					return e.getName();
				}
			}
		}
		return "";
	}

	public static List<SelectDto> getList() {
    	List<SelectDto> list = new ArrayList<SelectDto>();
		for (OtcOrderPayEnum e : OtcOrderPayEnum.values()) {
			list.add(new SelectDto(e.getCode(), e.getName()));
		}
		return list;
	}

}
