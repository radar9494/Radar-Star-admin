package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class CurrencyModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *名称
	 */
	private String name;
	
	/**
	 * 图标地址
	 */
	private String pic;

	/**
	 * 显示位置
	 */
	private Integer position;

	/**
	 * 状态 1启用 2不启用
	 */
	private Integer status;

	/**
	 *协议
	 */
	private Integer protocol;

	/**
	 * 第三方充提币种名称
	 */
	private String thirdCurrency;
	/**
	 * 区块确认数
	 */
	private Integer confirm;

	/**
	 *协议
	 */
	private Integer protocol2;

	/**
	 * 第三方充提币种名称
	 */
	private String thirdCurrency2;
	/**
	 * 区块确认数
	 */
	private Integer confirm2;
}
