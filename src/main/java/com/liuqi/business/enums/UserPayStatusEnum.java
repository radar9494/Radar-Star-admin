package com.liuqi.business.enums;

import java.util.ArrayList;
import java.util.List;
import com.liuqi.business.dto.SelectDto;


public enum UserPayStatusEnum {

	NOUSING("未启用", 0),
	USING("启用", 1);

	private String name;
	private Integer code;

	UserPayStatusEnum(String name, int code) {
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
			for (UserPayStatusEnum e : UserPayStatusEnum.values()) {
				if (e.getCode().equals(code)) {
					return e.getName();
				}
			}
		}
		return "";
	}

	public static List<SelectDto> getList() {
    	List<SelectDto> list = new ArrayList<SelectDto>();
		for (UserPayStatusEnum e : UserPayStatusEnum.values()) {
			list.add(new SelectDto(e.getCode(), e.getName()));
		}
		return list;
	}

}
