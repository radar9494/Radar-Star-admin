package com.liuqi.business.enums;

import java.util.ArrayList;
import java.util.List;
import com.liuqi.business.dto.SelectDto;


public enum OtcOrderRecordStatusEnum {

	WAIT("待接单", 0),
	WAITPAY("待支付", 1),
	WAITGATHERING("待收款", 2),
	COMPLETE("完成", 3),
	CANCEL("取消", 4),
	APPEAL("申诉", 5),
	APPEALSUCCESS("申诉成功", 6),
	APPEALFAIL("申诉失败", 7);

	private String name;
	private Integer code;

	OtcOrderRecordStatusEnum(String name, int code) {
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
			for (OtcOrderRecordStatusEnum e : OtcOrderRecordStatusEnum.values()) {
				if (e.getCode().equals(code)) {
					return e.getName();
				}
			}
		}
		return "";
	}

	public static List<SelectDto> getList() {
    	List<SelectDto> list = new ArrayList<SelectDto>();
		for (OtcOrderRecordStatusEnum e : OtcOrderRecordStatusEnum.values()) {
			list.add(new SelectDto(e.getCode(), e.getName()));
		}
		return list;
	}

}
