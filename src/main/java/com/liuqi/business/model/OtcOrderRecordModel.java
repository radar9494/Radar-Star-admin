package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;


@Data
public class OtcOrderRecordModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *订单id
	 */
	
	private Long orderId;
	
	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *买用户id
	 */
	
	private Long buyUserId;
	
	/**
	 *卖用户id
	 */
	
	private Long sellUserId;
	
	/**
	 *类型0买 1卖
	 */
	
	private Integer type;
	
	/**
	 *价格
	 */
	
	private BigDecimal price;
	
	/**
	 *数量
	 */
	
	private BigDecimal quantity;
	
	/**
	 *总金额
	 */
	
	private BigDecimal money;
	
	/**
	 *状态（0待接单，1待支付，2待收款，3完成 4取消 5申诉 6申诉成功 7申诉失败）
	 */
	
	private Integer status;
	
	/**
	 *打款唯一码
	 */
	
	private String memo;
	
	/**
	 *申诉方向0买 1卖
	 */
	
	private Integer appealType;
	
	/**
	 *申诉人
	 */
	
	private Long appealUser;
	
	/**
	 *申诉内容
	 */
	
	private String appealContent;
	/**
	 * 最终完成状态  0取消 1完成
	 */
	private Integer successStatus;


	private String orderNo;

	private Integer payType;
}
