package com.liuqi.business.enums;

import java.util.ArrayList;
import java.util.List;
import com.liuqi.business.dto.SelectDto;


public enum PledgeWalletLogTypeEnum {

	PLEDGE("质押", 0),
	PLEDGE_CANCEL("质押取消", 1);

	private String name;
	private Integer code;

	PledgeWalletLogTypeEnum(String name, int code) {
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
			for (PledgeWalletLogTypeEnum e : PledgeWalletLogTypeEnum.values()) {
				if (e.getCode().equals(code)) {
					return e.getName();
				}
			}
		}
		return "";
	}

	public static List<SelectDto> getList() {
    	List<SelectDto> list = new ArrayList<SelectDto>();
		for (PledgeWalletLogTypeEnum e : PledgeWalletLogTypeEnum.values()) {
			list.add(new SelectDto(e.getCode(), e.getName()));
		}
		return list;
	}
	public static boolean exist(Integer code){
		if (code != null) {
			for (PledgeWalletLogTypeEnum e : PledgeWalletLogTypeEnum.values()) {
				if (e.getCode().equals(code)) {
					return true;
				}
			}
		}
		return false;
	}

}
