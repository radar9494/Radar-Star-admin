package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class UserStoreModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户id
	 */
	
	private Long userId;
	
	/**
	 *状态 0未启用 1启用
	 */
	
	private Integer status;
	
	/**
	 *姓名
	 */
	
	private String name;
	
	/**
	 *卡号
	 */
	
	private String bankNo;
	
	/**
	 *地址
	 */
	
	private String bankAddress;
	
	/**
	 *银行名称
	 */
	
	private String bankName;
	


}
