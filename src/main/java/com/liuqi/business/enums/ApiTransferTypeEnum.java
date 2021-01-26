package com.liuqi.business.enums;

import java.util.ArrayList;
import java.util.List;
import com.liuqi.business.dto.SelectDto;


public enum ApiTransferTypeEnum {

	USING("可用", 0),
	LOCKING("锁仓", 1);

	private String name;
	private Integer code;

	ApiTransferTypeEnum(String name, int code) {
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
			for (ApiTransferTypeEnum e : ApiTransferTypeEnum.values()) {
				if (e.getCode().equals(code)) {
					return e.getName();
				}
			}
		}
		return "";
	}

	public static List<SelectDto> getList() {
    	List<SelectDto> list = new ArrayList<SelectDto>();
		for (ApiTransferTypeEnum e : ApiTransferTypeEnum.values()) {
			list.add(new SelectDto(e.getCode(), e.getName()));
		}
		return list;
	}

}
