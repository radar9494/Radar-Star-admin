package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class MiningWalletLogModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户
	 */
	
	private Long userId;
	
	/**
	 *数量
	 */
	
	private BigDecimal num;
	
	/**
	 *
	 */
	
	private Long currencyId;
	
	/**
	 *类型
	 */
	
	private Integer type;
	
	/**
	 *余额
	 */
	
	private BigDecimal balance;
	
	/**
	 *快照
	 */
	
	private String snapshot;
	


}
