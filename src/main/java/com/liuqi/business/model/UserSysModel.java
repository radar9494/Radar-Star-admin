package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;


@Data
public class UserSysModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *名称
	 */
	private String name;
	
	/**
	 *密码
	 */
	private String pwd;
	
	/**
	 *0未启用 1启用 2冻结
	 */
	private Integer status;
	
	/**
	 *币种id
	 */
	private Long currencyId;
	


}
