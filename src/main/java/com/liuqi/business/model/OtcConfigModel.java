package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;


@Data
public class OtcConfigModel extends BaseModel {

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *买开关0关闭 1开启
	 */
	
	private Integer buySwitch;
	
	/**
	 *卖开关0关闭 1开启
	 */
	
	private Integer sellSwitch;

	/**
	 * 接单超时时间
	 */
	private Integer waitTime;
	/**
	 * 支付超时时间
	 */
	private Integer payTime;

	/**
     * 位置
	 */
	private Integer position;

}
