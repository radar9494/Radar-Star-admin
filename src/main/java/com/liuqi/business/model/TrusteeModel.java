package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TrusteeModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *发布人
	 */
	private Long userId;
	
	/**
	 *币种交易id
	 */
	@NotNull(message = "交易对不能为空")
	private Long tradeId;
	
	/**
	 *交易类型
	 */
	@NotNull(message = "交易类型不能为空")
	private Integer tradeType;
	
	/**
	 *数量
	 */
	@NotNull(message = "数量不能为空")
	@DecimalMin(value = "0",message = "数量最小为0")
	private BigDecimal quantity;
	
	/**
	 *挂单价格
	 */
	@NotNull(message = "价格不能为空")
	@DecimalMin(value = "0",message = "价格最小为0")
	private BigDecimal price;
	
	/**
	 *已交易数量
	 */
	private BigDecimal tradeQuantity;
	
	/**
	 *状态TrusteeStatusEnum  0未交易  1交易完毕 2取消交易 3异常
	 */
	private Integer status;
	
	/**
	 *优先级
	 */
	private Integer priority;

	/**
	 *手续费比率
	 */
	private BigDecimal rate;

	/**
	 *白名单YesNoEnum 0不是 1是
	 */
	private Integer white;

	/**
	 * 机器人YesNoEnum 0不是 1是
	 */
	private Integer robot;


}
