package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;


@Data
public class FinancingRecordModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *配置id
	 */
	
	private Long configId;
	
	/**
	 *用户id
	 */
	
	private Long userId;
	
	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *融资币种id
	 */
	
	private Long financingCurrencyId;
	
	/**
	 *融资数量
	 */
	
	private BigDecimal quantity;
	
	/**
	 *获取币种数量
	 */
	
	private BigDecimal grantQuantity;
	
	/**
	 *状态 0未发放 1已发放
	 */
	
	private Integer status;
	


}
