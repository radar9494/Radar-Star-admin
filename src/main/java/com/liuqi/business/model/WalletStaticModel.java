package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class WalletStaticModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *可用
	 */
	
	private BigDecimal using;
	
	/**
	 *冻结
	 */
	
	private BigDecimal freeze;
	
	/**
	 *矿池投入数量
	 */
	
	private Long total;

	private Integer type;


}
