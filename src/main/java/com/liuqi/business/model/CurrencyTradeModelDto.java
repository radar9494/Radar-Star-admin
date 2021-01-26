package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liuqi.business.enums.SwitchEnum;
import lombok.Data;


@Data
public class CurrencyTradeModelDto extends CurrencyTradeModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	//状态 0停用 1启用
	public  final static int STATUS_STOP=0;
	public  final static int STATUS_START=1;

	//1主创区/2原创区
	public  final static int AREA_Z=1;
	public  final static int AREA_Y=2;
	//区域名称
	private String areaName;
	//区域币种名称
	private String currencyName;
	//交易币种名称
	private String tradeCurrencyName;
	//1主创区/2原创区
	private String areaStr;
	//状态 0停用 1启用
	private String statusStr;
	public String getStatusStr() {
		statusStr="停用";
		if(super.getStatus()!=null && super.getStatus()==STATUS_START){
			statusStr="启用";
		}
		return statusStr;
	}
	public String getAreaStr() {
		areaStr="主创区";
		if(super.getArea()!=null && super.getArea()==AREA_Y){
			statusStr="原创区";
		}
		return areaStr;
	}


	private String tradeSwitchStr;
	private String priceSwitchStr;
	private String quantitySwitchStr;
	private String limitSwitchStr;
	private String virtualSwitchStr;

	public String getTradeSwitchStr() {
		return SwitchEnum.getName(super.getTradeSwitch());
	}

	public String getPriceSwitchStr() {
		return SwitchEnum.getName(super.getPriceSwitch());
	}

	public String getQuantitySwitchStr() {
		return SwitchEnum.getName(super.getQuantitySwitch());
	}

	public String getLimitSwitchStr() {
		return SwitchEnum.getName(super.getLimitSwitch());
	}
	public String getVirtualSwitchStr() {
		return SwitchEnum.getName(super.getVirtualSwitch());
	}


	@JsonIgnore
	private String sortName="position asc,t.id";
	@JsonIgnore
	private String sortType="asc";
}
