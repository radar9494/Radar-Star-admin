package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class ApiTransferModel extends BaseModel{

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
	 *0未审核 1审核通过 2拒绝
	 */
	
	private Integer status;
	
	/**
	 *类型0可用 1锁仓
	 */
	
	private Integer type;
	
	/**
	 *转入名称
	 */
	
	private String name;
	
	/**
	 *外部编号唯一码
	 */
	
	private String num;
	
	/**
	 *外部转入人
	 */
	
	private String transferName;
	


}
