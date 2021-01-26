package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class TradeRecordModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *交易表id
	 */
	private Long tradeId;
	
	/**
	 *发布人id
	 */
	private Long sellUserId;
	
	/**
	 *交易类型
	 */
	private Long sellTrusteeId;
	
	/**
	 *数量
	 */
	private Long buyUserId;
	
	/**
	 *挂单价格
	 */
	private Long buyTrusteeId;
	
	/**
	 *已交易数量
	 */
	private BigDecimal tradeQuantity;
	
	/**
	 *成交价格
	 */
	private BigDecimal tradePrice;
	
	/**
	 *卖单手续费（数量）
	 */
	private BigDecimal sellCharge;
	
	/**
	 *买单手续费（数量）
	 */
	private BigDecimal buyCharge;
	
	/**
	 *托管买价格
	 */
	private BigDecimal buyPrice;
	
	/**
	 *托管卖价格
	 */
	private BigDecimal sellPrice;
	
	/**
	 *0买  1卖
	 */
	private Integer tradeType;

	/**
	 * 机器人YesNoEnum 0不是 1是
	 */
	private Integer robot;

	/**
	 * 买钱包状态0未处理 1已处理 2处理失败
	 */
	private  Integer buyWalletStatus;
	/**
	 * 卖钱包状态0未处理 1已处理 2处理失败
	 */
	private  Integer sellWalletStatus;
}
