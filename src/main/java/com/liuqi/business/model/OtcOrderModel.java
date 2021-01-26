package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class OtcOrderModel extends BaseModel {

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *用户id
	 */
	
	private Long userId;
	
	/**
	 *类型0买 1卖
	 */
	
	private Integer type;
	
	/**
	 *价格
	 */
	
	private BigDecimal price;
	
	/**
	 *发布数量
	 */
	
	private BigDecimal quantity;
	
	/**
	 *已交易数量
	 */
	
	private BigDecimal tradeQuantity;
	
	/**
	 *最小数量
	 */
	
	private BigDecimal min;
	
	/**
	 *最大数量
	 */
	
	private BigDecimal max;
	
	/**
	 *银行卡0不支持 1支持
	 */
	
	private Integer yhk;
	
	/**
	 *支付宝0不支持 1支持
	 */
	
	private Integer zfb;
	
	/**
	 *微信0不支持 1支持
	 */
	
	private Integer wx;
	
	/**
	 *状态（0待交易，1交易结束）
	 */
	private Integer status;

	/**
	 *状态（0下架，1上架）
	 */
	private Integer cancel;


}
