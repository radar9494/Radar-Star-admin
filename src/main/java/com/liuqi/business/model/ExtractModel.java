package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@Data
public class ExtractModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户
	 */
	private Long userId;
	
	/**
	 *币种
	 */
	@NotNull(message = "币种不能为空")
	private Long currencyId;
	
	/**
	 *申请数量
	 */
	@NotNull(message = "数量不能为空")
	@DecimalMin(value = "0",message = "数量不能小于0")
	private BigDecimal quantity;

	//冻结数量
	private BigDecimal freezeQuantity;
	/**
	 *到账数量
	 */
	private BigDecimal realQuantity;
	/**
	 *地址
	 */
	@NotNull(message = "地址不能为空")
	private String address;

	/**
	 *手续费
	 */
	private BigDecimal rate;
	
	/**
	 *状态 0未处理 1已处理
	 */
	private Integer status;
	
	/**
	 *提现hash
	 */
	private String hash;

	//处理时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date dealDate;

	//标签
	private String memo;

	//理由
	private String reason;

	//0外部  1内部
	private Integer type;

	//接收人
	private Long receiveUserId;

	//0扣除余额 1扣除提现余额
	private Long dealAdminId;
	//协议
	private Integer protocol;
	//提现 0可用 1锁仓
	private Integer walletType;

	private Long rateCurrencyId;
}
