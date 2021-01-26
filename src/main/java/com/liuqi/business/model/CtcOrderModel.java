package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.liuqi.base.BaseModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CtcOrderModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户id
	 */
	
	private Long userId;
	
	/**
	 *类型0买 2卖
	 */

	private Integer tradeType;

	/**
	 * 币种id
	 */

	private Long currencyId;

	/**
	 * 承运商id
	 */

	private Long storeId;
	
	/**
	 *价格
	 */
	
	private BigDecimal price;
	
	/**
	 *数量
	 */
	
	private BigDecimal quantity;

	/**
	 * 总金额
	 */

	private BigDecimal money;

	/**
	 *状态（处理中，处理完成，取消）
	 */
	
	private Integer status;

	/**
	 * 打款唯一码
	 */

	private String memo;

	/**
	 * 自动结束时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date autoEndTime;


	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 卡号
	 */
	private String bankNo;
	/**
	 * 地址
	 */
	private String bankAddress;
	/**
	 * 银行名称
	 */
	private String bankName;

}
