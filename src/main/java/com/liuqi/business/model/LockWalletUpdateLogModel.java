package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LockWalletUpdateLogModel extends BaseModel{

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
	 *更新前锁仓币数量
	 */
	private BigDecimal oldLock;
	/**
	 *更新后锁仓币数量
	 */
	private BigDecimal modifyLock;
	/**
	 *更新后锁仓币数量
	 */
	private BigDecimal newLock;
	
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
	


}
