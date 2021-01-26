package com.liuqi.business.enums;

import java.util.ArrayList;
import java.util.List;
import com.liuqi.business.dto.SelectDto;


public enum MessageStatusEnum {

	NOTREAD("未读", 0),
	READ("已读", 1);

	private String name;
	private Integer code;

	MessageStatusEnum(String name, int code) {
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
			for (MessageStatusEnum e : MessageStatusEnum.values()) {
				if (e.getCode().equals(code)) {
					return e.getName();
				}
			}
		}
		return "";
	}

	public static List<SelectDto> getList() {
    	List<SelectDto> list = new ArrayList<SelectDto>();
		for (MessageStatusEnum e : MessageStatusEnum.values()) {
			list.add(new SelectDto(e.getCode(), e.getName()));
		}
		return list;
	}

}
