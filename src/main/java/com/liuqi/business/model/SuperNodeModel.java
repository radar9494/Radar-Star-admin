package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class SuperNodeModel extends BaseModel{

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
	 *编号
	 */
	
	private String num;
	
	/**
	 *推荐人
	 */
	
	private Long recommendUserId;
	


}
