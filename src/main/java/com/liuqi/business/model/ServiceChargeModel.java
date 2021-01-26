package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;


@Data
public class ServiceChargeModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *统计日期
	 */
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
	private Date calcDate;
	
	/**
	 *币种id
	 */
	private Long currencyId;
	
	/**
	 *手续费
	 */
	private BigDecimal charge;

	/**
	 * 手续费
	 */
	private BigDecimal snapPrice;

}
