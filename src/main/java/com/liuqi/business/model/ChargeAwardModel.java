package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class ChargeAwardModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


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
	 *层数
	 */
	
	private Integer level;
	
	/**
	 *释放订单id
	 */
	
	private Long orderId;
	
	/**
	 *成交订单id
	 */
	
	private Long recordId;
	
	/**
	 *快照手续币种
	 */
	
	private Long snapChargeCurrency;
	
	/**
	 *快照手续费数量(原手续费)
	 */
	
	private BigDecimal snapCharge;
	
	/**
	 *快照币种数量
	 */
	
	private BigDecimal snapPrice;

	/**
	 * 快照分配币种价格
	 */
	private BigDecimal snapAwardPrice;


}
