package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class RaiseRecordModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *兑换币种id
	 */
	
	private Long exchangeCurrencyId;
	
	/**
	 *用户id
	 */
	
	private Long userId;
	
	/**
	 *价格
	 */
	
	private BigDecimal price;
	
	/**
	 *数量
	 */
	
	private BigDecimal quantity;
	
	/**
	 *金额
	 */
	
	private BigDecimal money;
	
	/**
	 *配置id
	 */
	
	private Long configId;
	


}
