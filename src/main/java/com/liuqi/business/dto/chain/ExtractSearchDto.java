package com.liuqi.business.dto.chain;

import com.liuqi.base.BaseModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExtractSearchDto extends BaseModel {

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *商户
	 */
	
	private Long storeId;
	
	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *商户订单
	 */
	
	private String orderNo;
	
	/**
	 *提现数量
	 */
	
	private BigDecimal quantity;
	
	/**
	 *接收地址
	 */
	
	private String toAddress;
	
	/**
	 *标签
	 */
	
	private String memo;
	
	/**
	 *交易hash
	 */
	
	private String hash;
	
	/**
	 *状态 0待处理 1处理中 2处理成功 3处理失败 4取消
	 */
	
	private Integer status;

	/**
	 * 发送地址
	 */
	private String sendAddress;
	


}
