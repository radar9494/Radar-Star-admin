package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class PledgeWalletLogModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户id
	 */
	
	private Long userId;
	
	/**
	 *类型
	 */
	
	private Integer type;
	
	/**
	 *金额 
	 */
	
	private BigDecimal money;
	
	/**
	 *余额 
	 */
	
	private BigDecimal balance;
	


}
