package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class TradeRecordUserModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *交易表id
	 */
	
	private Long tradeId;
	
	/**
	 *交易用户
	 */
	
	private Long userId;
	
	/**
	 *交易类型
	 */
	
	private Long trusteeId;
	
	/**
	 *已交易数量
	 */
	
	private BigDecimal tradeQuantity;
	
	/**
	 *成交价格
	 */
	
	private BigDecimal tradePrice;
	
	/**
	 *手续费
	 */
	
	private BigDecimal charge;
	
	/**
	 *托管价格
	 */
	
	private BigDecimal price;
	
	/**
	 *机器人 1是 2不是
	 */
	
	private Integer robot;
	
	/**
	 *0买  1卖
	 */
	
	private Integer tradeType;
	


}
