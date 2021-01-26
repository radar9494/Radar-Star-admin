package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class ChargeAwardConfigModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *手续费奖励开关0关 1开
	 */
	
	private Integer onOff;
	
	/**
	 *奖励币种0 交易币种  否则指定币种
	 */
	
	private Long awardCurrency;
	
	/**
	 *奖励层数
	 */
	
	private Integer awardLevel;
	
	/**
	 *奖励信息
	 */
	
	private String awardInfo;
	


}
