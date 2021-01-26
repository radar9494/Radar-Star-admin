package com.liuqi.business.model;

import lombok.Data;

import java.sql.Time;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class ApiTransferConfigModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *外部名称
	 */
	
	private String name;
	
	/**
	 *私钥
	 */
	
	private String key;
	
	/**
	 *开关0关 1开
	 */
	
	private Integer onOff;
	
	/**
	 *开始时间
	 */
	@JsonFormat(pattern = "HH:mm:ss",timezone="GMT+8")
	private Time startTime;
	
	/**
	 *结束时间
	 */
	@JsonFormat(pattern = "HH:mm:ss",timezone="GMT+8")
	private Time endTime;
	
	/**
	 *每天最大数量
	 */
	
	private BigDecimal dayMaxQuantity;
	
	/**
	 *最大次数
	 */
	
	private Integer dayTimes;
	
	/**
	 *每次最大数量
	 */
	
	private BigDecimal timesQuantity;
	
	/**
	 *转入币种
	 */
	
	private String currencyIds;
	
	/**
	 *类型0可用 1锁仓
	 */
	
	private Integer type;

	/**
	 * 自动确认开关 0关 1开
	 */
	private Integer autoConfirm;

}
