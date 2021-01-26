package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;


@Data
public class UserAuthModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *
	 */
	private Long userId;
	
	/**
	 *真实实名
	 */
	private String realName;
	
	/**
	 *身份证号码
	 */
	private String idcart;
	
	/**
	 *状态 0未认证  1认证中  2已认证 3认证失败 
	 */
	private Integer authStatus;
	
	/**
	 *身份证正面
	 */
	private String image1;
	
	/**
	 *身份证反面
	 */
	private String image2;
	
	/**
	 *身份证手持
	 */
	private String image3;
	


}
