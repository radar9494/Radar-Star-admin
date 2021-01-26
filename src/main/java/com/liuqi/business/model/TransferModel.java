package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class TransferModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户id
	 */
	
	private Long userId;
	
	/**
	 *接受id
	 */
	
	private Long receiveId;
	
	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *手续费
	 */
	
	private BigDecimal rate;
	
	/**
	 *数量
	 */
	
	private BigDecimal quantity;
	
	/**
	 *兑换数量
	 */
	
	private BigDecimal realQuantity;

    private String phone;

}
