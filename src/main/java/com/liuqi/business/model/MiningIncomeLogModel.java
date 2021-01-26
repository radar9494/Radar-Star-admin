package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MiningIncomeLogModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户
	 */
	
	private Long userId;
	
	/**
	 *收益数量
	 */
	
	private BigDecimal num;
	
	/**
	 *类型  0 静态 1动态 2时间戳
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

	private LocalDate date;
	


}
