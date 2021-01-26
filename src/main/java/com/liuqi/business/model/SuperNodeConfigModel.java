package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class SuperNodeConfigModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *分红开关0关 1开
	 */
	
	private Integer releaseOnoff;
	
	/**
	 *总手续费分红比例%
	 */
	
	private BigDecimal releaseRate;
	
	/**
	 *分红结算币种
	 */
	
	private Long releaseCurrencyId;
	
	/**
	 *节点总人数
	 */
	
	private Integer count;
	
	/**
	 *参与开关0关 1开
	 */
	
	private Integer joinOnoff;
	
	/**
	 *参与扣除币种
	 */
	
	private Long joinCurrencyId;
	
	/**
	 *参与扣除币种数量
	 */
	
	private BigDecimal joinQuantity;
	


}
