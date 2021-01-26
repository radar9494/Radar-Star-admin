package com.liuqi.business.enums;

import java.util.ArrayList;
import java.util.List;
import com.liuqi.business.dto.SelectDto;


public enum TransferLogTypeEnum {
//0 币币到OTC 1币币到矿池 2 OTC到币币 3 矿池到币币
	ONE("币币到OTC", 0),
	TWO("币币到矿池", 1),
	THREE("OTC到币币", 2),
	FOUR("矿池到币币", 3),
	;

	private String name;
	private Integer code;

	TransferLogTypeEnum(String name, int code) {
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
			for (TransferLogTypeEnum e : TransferLogTypeEnum.values()) {
				if (e.getCode().equals(code)) {
					return e.getName();
				}
			}
		}
		return "";
	}

	public static List<SelectDto> getList() {
    	List<SelectDto> list = new ArrayList<SelectDto>();
		for (TransferLogTypeEnum e : TransferLogTypeEnum.values()) {
			list.add(new SelectDto(e.getCode(), e.getName()));
		}
		return list;
	}
	public static boolean exist(Integer code){
		if (code != null) {
			for (TransferLogTypeEnum e : TransferLogTypeEnum.values()) {
				if (e.getCode().equals(code)) {
					return true;
				}
			}
		}
		return false;
	}

}
