package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

@Data
public class UserAdminLoginModel extends BaseModel {

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *登录用户
	 */
	
	private String adminName;
	
	/**
	 *登录ip
	 */
	
	private String ip;
	
	/**
	 *登录城市
	 */
	
	private String city;
	


}
