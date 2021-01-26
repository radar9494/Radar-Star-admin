package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class UserWalletAddressModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户id
	 */
	private Long userId;
	
	/**
	 *币种id
	 */
	private Long currencyId;
	
	/**
	 *地址
	 */
	private String address;
	/**
	 * 标签
	 */
	private String memo;

}
