package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;


public enum MessageTypeEnum {

	SYS("系统", 0),
	LOCK_ZORE("锁仓币为0", 1),
	LOCK_MAX_TIMES_BUY("已达释放次数", 2),
	LOCK_MAX_TIMES_SELL("已达释放次数", 3),
	LOCK_MAX_QUANTITY_BUY("已达释放数量", 4),
	LOCK_MAX_QUANTITY_SELL("已达释放数量", 5);

	private String name;
	private Integer code;

	MessageTypeEnum(String name, int code) {
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
			for (MessageTypeEnum e : MessageTypeEnum.values()) {
				if (e.getCode().equals(code)) {
					return e.getName();
				}
			}
		}
		return "";
	}

	public static List<SelectDto> getList() {
    	List<SelectDto> list = new ArrayList<SelectDto>();
		for (MessageTypeEnum e : MessageTypeEnum.values()) {
			list.add(new SelectDto(e.getCode(), e.getName()));
		}
		return list;
	}

}
