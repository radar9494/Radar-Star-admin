package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class KDataModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *类型 1::1m 2:5m 3:15m 4:30m 5:1h 6:1d 7:1week
	 */
	private Integer type;
	
	/**
	 *开盘价格
	 */
	private BigDecimal openPrice;
	
	/**
	 *关盘价格
	 */
	private BigDecimal closePrice;
	
	/**
	 *最高价格
	 */
	private BigDecimal maxPrice;
	
	/**
	 *最低价格
	 */
	private BigDecimal minPrice;
	
	/**
	 *成交数量
	 */
	private BigDecimal nums;
	
	/**
	 *币种交易id
	 */
	private Long tradeId;

	//时间
	private Date time;


}
