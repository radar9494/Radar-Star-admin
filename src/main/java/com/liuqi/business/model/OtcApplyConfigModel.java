package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class OtcApplyConfigModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *质押所需数量
	 */
	
	private BigDecimal quantity;
	
	/**
	 *手续费
	 */
	
	private BigDecimal rate;
	
	/**
	 *手续费币种id
	 */
	
	private Long rateCurrencyId;
	


}
