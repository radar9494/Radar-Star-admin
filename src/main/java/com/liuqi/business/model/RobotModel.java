package com.liuqi.business.model;

import com.alibaba.fastjson.JSON;
import com.liuqi.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
public class RobotModel extends BaseModel {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 订单用户id
	 */
	private Long userId;

	/**
	 * 交易对id
	 */
	private Long tradeId;

	/**
	 * 类型 0市值管理机器人 1随大盘机器人
	 */
	private Integer robotType;


	/**
	 * 单据类型 0真实交易  1虚拟交易
	 */
	private Integer runType;


	/**
	 * 间隔时间
	 */
	private Integer interval;

	/**
	 * 失败次数
	 */
	private Integer failTime;


	//买开关 0关1开
	private Integer buySwitch;
	//卖开关 0关1开
	private Integer sellSwitch;
	//钱包开关 0关 1开
	private Integer walletSwitch;

	/**
	 * 基准价
	 */
	private BigDecimal basePrice;

	/**
	 * 最小随机价格
	 */
	private BigDecimal minBuyPrice;

	/**
	 * 最大随机价格
	 */
	private BigDecimal maxBuyPrice;

	/**
	 * 最小随机价格
	 */
	private BigDecimal minSellPrice;

	/**
	 * 最大随机价格
	 */
	private BigDecimal maxSellPrice;
	/**
	 * 数量随机区间
	 */
	private List<Quantity> quantityInterval;

	private String quantityIntervalString;

	/**
	 * 买涨幅度
	 */
	private Integer buyRise;
	/**
	 * 买跌幅度
	 */
	private Integer buyFall;
	/**
	 * 卖涨幅度
	 */
	private Integer sellRise;
	/**
	 * 卖跌幅度
	 */
	private Integer sellFall;

	/**
	 * 数量区间替换时间
	 */
	private Long changeIntervalTime;

	/**
	 * 随大盘机器人 价格乘数
	 */
	private BigDecimal  priceMultiplier;
	/**
	 * 随大盘机器人 数量乘数
	 */
	private BigDecimal  quantityMultiplier;

	/**
	 * 0 非主流 , 1 主流币种
	 */
	private Byte mainstream;

	/**
	 * 涨跌幅偏差
	 */
	private BigDecimal variationDeviation;



	public List<Quantity> getQuantityInterval() {
		return JSON.parseArray(quantityIntervalString, Quantity.class);
	}

	public void setQuantityInterval() {
		JSON.parseArray(quantityIntervalString, Quantity.class);
	}

	public String getQuantityIntervalString() {
		return quantityIntervalString;
	}

	public void setQuantityIntervalString(String quantityIntervalString) {
		this.quantityInterval = JSON.parseArray(quantityIntervalString, Quantity.class);
		this.quantityIntervalString = quantityIntervalString;
	}

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Quantity {

		/**
		 * 最小随机交易量
		 */
		private BigDecimal minQuantity;
		/**
		 * 最大随机交易量
		 */
		private BigDecimal maxQuantity;

		/**
		 * 状态 0 关 1 开
		 */
		private Integer sectionState;
	}
}
