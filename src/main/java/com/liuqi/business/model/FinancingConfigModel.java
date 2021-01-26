package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;


@Data
public class FinancingConfigModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *总额度
	 */
	
	private BigDecimal quantity;
	
	/**
	 *已融资额度 计算融资币的数额
	 */
	
	private BigDecimal curQuantity;
	
	/**
	 *融资币种id
	 */
	
	private Long financingCurrencyId;
	
	/**
	 *兑换比例（1融资币种：币种）
	 */
	
	private BigDecimal exchange;
	
	/**
	 *开始时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date startTime;
	
	/**
	 *结束时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date endTime;
	
	/**
	 *每次最小数量
	 */
	
	private BigDecimal min;
	
	/**
	 *每次最大数量
	 */
	
	private BigDecimal max;
	
	/**
	 *发放类型 0直接发放 1结束统一发放
	 */
	
	private Integer grantType;
	
	/**
	 *状态 0未开始 1进行中 2结束
	 */
	
	private Integer status;
	


}
