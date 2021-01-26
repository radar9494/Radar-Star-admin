package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class RechargeModel extends BaseModel{

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
	 *充值数量
	 */
	private BigDecimal quantity;
	
	/**
	 *状态 0未处理 1已处理
	 */
	private Integer status;
	
	/**
	 *收款钱包
	 */
	private String address;

	//处理时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date dealDate;

	private String hash;

	//0外部  1内部
	private Integer type;

	//0未推送 1已推送
	private Integer sendType;

	//协议
	private Integer protocol;
	//提现 0可用 1锁仓
	private Integer walletType;
}
