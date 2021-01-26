package com.liuqi.business.model;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExprGroup;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class UserWalletModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户id
	 */
	private Long userId;
	
	/**
	 *币种id
	 */
	private Long currencyId;
	
	/**
	 *可用数量
	 */
	private BigDecimal using;
	
	/**
	 *冻结数量
	 */
	private BigDecimal freeze;

	/**
	 * @Author 秦始皇188世
	 * @Description 开关 0关1开
	 * @Date 2020/7/11 14:37
	 * @Version 2020 Ultimate Edition
	 */
	private Integer off;

	private Integer gatewaySwitch;

	private BigDecimal cnyQuantity;


}
