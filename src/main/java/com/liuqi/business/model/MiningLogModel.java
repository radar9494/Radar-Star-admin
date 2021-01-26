package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MiningLogModel extends BaseModel{

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
	 *类型
	 */
	
	private Byte type;
	
	/**
	 *
	 */
	
	private Long currencyId;
	
	/**
	 *币种
	 */
	
	private String currencyName;

	private Byte state;
	


}
