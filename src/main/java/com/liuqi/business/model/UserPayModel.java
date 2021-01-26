package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;


@Data
public class UserPayModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户id
	 */
	private Long userId;
	
	/**
	 *付款类型1银行卡 2支付宝 3微信
	 */
	private Integer payType;
	
	/**
	 *用户名
	 */
	private String name;
	
	/**
	 *账号
	 */
	private String no;
	
	/**
	 *银行地址
	 */
	private String bankAddress;
	
	/**
	 *银行名称
	 */
	private String bankName;
	
	/**
	 *收款二维码
	 */
	private String pic;
	
	/**
	 *0未启用 1启用
	 */
	private Integer status;
	


}
