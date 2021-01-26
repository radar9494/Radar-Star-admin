package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class CurrencyTradeModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种名称
	 */
	private Long currencyId;
	
	/**
	 *交易币种
	 */
	private Long tradeCurrencyId;
	
	/**
	 *交易区域
	 */
	private Long areaId;
	
	/**
	 *显示位置
	 */
	private Integer position;
	
	/**
	 *1主创区/2原创区
	 */
	private Integer area;
	
	/**
	 *接口查询名称
	 */
	private String searchName;
	
	/**
	 *状态 0停用 1启用
	 */
	private Integer status;

	/**
	 *交易开关（0关 1开）
	 */
	private Integer tradeSwitch;
	/**
	 *价格开关（0关 1开）
	 */
	private Integer priceSwitch;
	/**
	 *数量开关（0关 1开）
	 */
	private Integer quantitySwitch;
	/**
	 *涨跌开关（0关 1开）
	 */
	private Integer limitSwitch;
	/**
	 *买手续费
	 */
	private BigDecimal buyRate;
	/**
	 *卖手续费
	 */
	private BigDecimal sellRate;
	/**
	 *最小价格
	 */
	private BigDecimal minPirce;
	/**
	 *最大价格
	 */
	private BigDecimal maxPirce;
	/**
	 *最小数量
	 */
	private BigDecimal minQuantity;
	/**
	 *最大数量
	 */
	private BigDecimal maxQuantity;
	/**
	 *涨跌百分比
	 */
	private BigDecimal limitRate;

	/**
	 * 输入限制价格小数位数
	 */
	private Integer digitsP;
	/**
	 * 输入限制数量小数位数
	 */
	private Integer digitsQ;

	/**
	 * 虚拟买卖盘开关 0关 1开
	 */
	private Integer virtualSwitch;
}
