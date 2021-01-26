package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class LockReleaseModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *释放时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
	private Date sendDate;
	
	/**
	 *用户id
	 */
	
	private Long userId;
	
	/**
	 *币种
	 */
	
	private Long currencyId;
	
	/**
	 *数量
	 */
	
	private BigDecimal quantity;
	
	/**
	 *0未发放 1已发放 2异常
	 */
	
	private Integer status;
	
	/**
	 *释放订单
	 */
	
	private Long orderId;
	/**
	 *释放类型
	 */

	private Integer tradeType;
	/**
	 *快照锁仓数量
	 */
	
	private BigDecimal snapLock;
	


}
