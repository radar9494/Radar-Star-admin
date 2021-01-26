package com.liuqi.business.model;

import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class LockConfigModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *锁仓币种
	 */
	
	private Long currencyId;
	
	/**
	 *释放交易对
	 */
	
	private Long tradeId;
	
	/**
	 *释放开始时间
	 */
	private Time startTime;
	
	/**
	 *释放结束时间
	 */
	private Time endTime;
	
	/**
	 *买释放开关0关 1开
	 */
	
	private Integer buySwitch;
	
	/**
	 *卖释放开关0关 1开
	 */
	
	private Integer sellSwitch;
	
	/**
	 *买每日最大释放次数
	 */
	
	private Integer buyTimes;
	
	/**
	 *买每次释放百分比%
	 */
	
	private BigDecimal buyTimesRate;
	
	/**
	 *买每天释放总百分比%
	 */
	
	private BigDecimal buyDayRate;
	
	/**
	 *买每天释放最大值
	 */
	
	private BigDecimal buyDayMax;


	/**
	 *卖每日最大释放次数
	 */

	private Integer sellTimes;

	/**
	 *卖每次释放百分比%
	 */

	private BigDecimal sellTimesRate;

	/**
	 *卖每天释放总百分比%
	 */

	private BigDecimal sellDayRate;

	/**
	 *卖每天释放最大值
	 */

	private BigDecimal sellDayMax;

}
