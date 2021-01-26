package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class LockTransferOutputModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *用户id
	 */
	
	private Long userId;
	
	/**
	 *申请数量
	 */
	
	private BigDecimal applyQuantity;
	
	/**
	 *到账数量
	 */
	
	private BigDecimal quantity;
	
	/**
	 *手续费(%)
	 */
	
	private BigDecimal rate;
	
	/**
	 *手续费
	 */
	
	private BigDecimal charge;
	
	/**
	 *接收用户
	 */
	
	private Long receiveUserId;
	


}
