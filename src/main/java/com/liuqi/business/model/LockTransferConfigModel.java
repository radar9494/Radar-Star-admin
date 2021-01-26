package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class LockTransferConfigModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *合约币种
	 */
	
	private Long currencyId;
	
	/**
	 *转入开关0关 1开
	 */
	
	private Integer inputSwitch;
	
	/**
	 *杠杆倍数
	 */
	
	private Integer lever;
	
	/**
	 *转让开关0关 1开
	 */
	
	private Integer transferSwitch;
	
	/**
	 *转让手续费(%)
	 */
	
	private BigDecimal transferRate;
	
	/**
	 *每天转让次数
	 */
	
	private Integer transferTimes;
	


}
