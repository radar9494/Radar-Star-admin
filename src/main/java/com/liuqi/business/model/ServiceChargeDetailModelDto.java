package com.liuqi.business.model;

import com.liuqi.business.enums.ChargeDetailTypeEnum;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class ServiceChargeDetailModelDto extends ServiceChargeDetailModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private String currencyName;

	private String typeStr;

	public String getTypeStr() {
		return ChargeDetailTypeEnum.getName(super.getType());
	}

	/**
	 * 生成手续费表
	 * @param orderId     订单
	 * @param charge      总手续费
	 * @param currencyId 币种
	 * @return
	 */
	public static ServiceChargeDetailModelDto buildServiceChargeDetailModel(Long orderId, BigDecimal charge, Long currencyId, Integer type){
		ServiceChargeDetailModelDto detail =new ServiceChargeDetailModelDto();
		detail.setCharge(charge);
		detail.setCurrencyId(currencyId);
		detail.setOrderId(orderId);
		detail.setType(type);
		return detail;
	}



}
