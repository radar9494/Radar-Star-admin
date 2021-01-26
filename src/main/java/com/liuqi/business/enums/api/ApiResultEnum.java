package com.liuqi.business.enums.api;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;


public enum ApiResultEnum {

	SUCCESS("成功", 0),
	NO_APPLY("用户未申请api", -1001),
	ERROR_API_NO_USING("api未启用", -1002),

	ERROR_SIGN("签名验证异常", -2001),
	ERROR_PERMISSION("无权限", -2002),
	ERROR_TIME("时间异常", -2003),
	ERROR_CURRENCY("币种异常", -2004),

	ERROR_PARAMS("参数异常", -3000),
	ERROR_HEAD_API("apikey参数异常", -3001),
	ERROR_HEAD_TIME("timestamp参数异常", -3002),
	ERROR_HEAD_SIGN("sign参数异常", -3003),


	ERROR_ORDER_NOT_EXITS("订单不存在", -4001),
	ERROR_PRICE_NOT_EXITS("价格不存在", -4002),

	ERROR("异常", -5001);

	private String name;
	private Integer code;

	ApiResultEnum(String name, int code) {
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
			for (ApiResultEnum e : ApiResultEnum.values()) {
				if (e.getCode().equals(code)) {
					return e.getName();
				}
			}
		}
		return "";
	}

	public static List<SelectDto> getList() {
    	List<SelectDto> list = new ArrayList<SelectDto>();
		for (ApiResultEnum e : ApiResultEnum.values()) {
			list.add(new SelectDto(e.getCode(), e.getName()));
		}
		return list;
	}

}
