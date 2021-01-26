package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class UserRechargeAddressModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户id
	 */
	private Long userId;
	

	/**
	 *充币地址
	 */
	private String address;
	
	/**
	 *充币路径
	 */
	private String path;
	
	/**
	 *协议
	 */
	private Integer protocol;
	


}
