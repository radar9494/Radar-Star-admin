package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class CurrencyAreaModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *区域名称
	 */
	private String name;
	
	/**
	 *显示位置
	 */
	private Integer position;
	
	/**
	 *状态 0停用 1启用
	 */
	private Integer status;
	
	/**
	 *币种id
	 */
	private Long currencyId;
	


}
