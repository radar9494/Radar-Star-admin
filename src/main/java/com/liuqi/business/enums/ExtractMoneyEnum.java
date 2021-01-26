package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 提现状态 0 处理中 1提现成功 2提现失败
 */
public enum ExtractMoneyEnum {

	APPLY_ING("提现申请中",0 ), APPLY_SUCCESS("申请成功",1), APPLY_FAIL("申请失败",2) ,APPLY_DOING("处理中",3),APPLY_ERROR("异常",4);

	private String name;
	private Integer code;

	ExtractMoneyEnum(String name, int code) {
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
			for (ExtractMoneyEnum e : ExtractMoneyEnum.values()) {
				if (e.getCode().equals(code)) {
					return e.getName();
				}
			}
		}
		return "";
	}

	public static List<SelectDto> getList() {
		List<SelectDto> list = new ArrayList<SelectDto>();
		for (ExtractMoneyEnum e : ExtractMoneyEnum.values()) {
			list.add(new SelectDto(e.getCode(), e.getName()));
		}
		return list;
	}

}
