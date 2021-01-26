package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class UserWalletUpdateLogModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户id
	 */
	private Long userId;
	/**
	 * 币种id
	 */
	private Long currencyId;

	/**
	 *更新前可使用币数量
	 */
	private BigDecimal oldUsing;
	/**
	 *更新后可使用币数量
	 */
	private BigDecimal modifyUsing;
	/**
	 *更新后可使用币数量
	 */
	private BigDecimal newUsing;
	
	/**
	 *更新前冻结币数量
	 */
	private BigDecimal oldFreeze;
	/**
	 *更新后冻结币数量
	 */
	private BigDecimal modifyFreeze;
	/**
	 *更新后冻结币数量
	 */
	private BigDecimal newFreeze;
	
	/**
	 *操作管理员id
	 */
	private Long adminId;
	
private Integer type;

}
