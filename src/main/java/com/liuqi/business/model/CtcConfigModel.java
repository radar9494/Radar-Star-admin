package com.liuqi.business.model;

import lombok.Data;

import java.sql.Time;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class CtcConfigModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *买开关0关闭 1开启
	 */
	
	private Integer buySwitch;
	
	/**
	 *卖开关0关闭 1开启
	 */
	
	private Integer sellSwitch;
	
	/**
	 *买最小数量
	 */
	
	private BigDecimal buyMin;
	
	/**
	 *卖最小数量
	 */
	
	private BigDecimal sellMin;
	
	/**
	 *买最大数量
	 */
	
	private BigDecimal buyMax;
	
	/**
	 *卖最大数量
	 */
	
	private BigDecimal sellMax;
	
	/**
	 *价格
	 */
	
	private BigDecimal price;
	
	/**
	 *zb查询价格字段
	 */
	
	private String outerPrice;
	
	/**
	 *买价格涨幅
	 */
	
	private BigDecimal buyRang;
	
	/**
	 *卖价格涨幅
	 */
	
	private BigDecimal sellRang;

	/**
	 * 开始时间
	 */
	private Time startTime;
	/**
	 * 结束时间
	 */
	private Time endTime;

}
