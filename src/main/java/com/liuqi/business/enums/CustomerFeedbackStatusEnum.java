package com.liuqi.business.enums;

import java.util.ArrayList;
import java.util.List;
import com.liuqi.business.dto.SelectDto;


public enum CustomerFeedbackStatusEnum {

	WAIT("未处理", 0),
	SUCCESS("已处理", 1);

	private String name;
	private Integer code;

	CustomerFeedbackStatusEnum(String name, int code) {
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
			for (CustomerFeedbackStatusEnum e : CustomerFeedbackStatusEnum.values()) {
				if (e.getCode().equals(code)) {
					return e.getName();
				}
			}
		}
		return "";
	}

	public static List<SelectDto> getList() {
    	List<SelectDto> list = new ArrayList<SelectDto>();
		for (CustomerFeedbackStatusEnum e : CustomerFeedbackStatusEnum.values()) {
			list.add(new SelectDto(e.getCode(), e.getName()));
		}
		return list;
	}
	public static boolean exist(Integer code){
		if (code != null) {
			for (CustomerFeedbackStatusEnum e : CustomerFeedbackStatusEnum.values()) {
				if (e.getCode().equals(code)) {
					return true;
				}
			}
		}
		return false;
	}

}
