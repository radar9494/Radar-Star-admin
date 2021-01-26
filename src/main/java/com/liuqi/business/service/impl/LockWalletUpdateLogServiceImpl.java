package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.mapper.LockWalletUpdateLogMapper;
import com.liuqi.business.model.LockWalletUpdateLogModel;
import com.liuqi.business.model.LockWalletUpdateLogModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.LockWalletUpdateLogService;
import com.liuqi.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Transactional(readOnly = true)
public class LockWalletUpdateLogServiceImpl extends BaseServiceImpl<LockWalletUpdateLogModel,LockWalletUpdateLogModelDto> implements LockWalletUpdateLogService {

	@Autowired
	private LockWalletUpdateLogMapper lockWalletUpdateLogMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private CurrencyService currencyService;

	@Override
	public BaseMapper<LockWalletUpdateLogModel,LockWalletUpdateLogModelDto> getBaseMapper() {
		return this.lockWalletUpdateLogMapper;
	}
	/**
	 * 添加记录
	 * @param oldLock
	 * @param modifyLock
	 * @param newLock
	 * @param oldFreeze
	 * @param modifyFreeze
	 * @param newFreeze
	 * @param adminId
	 * @param userId
	 * @param currencyId
	 */
	@Override
	@Transactional
	public void insert(BigDecimal oldLock, BigDecimal modifyLock, BigDecimal newLock,
				BigDecimal oldFreeze, BigDecimal modifyFreeze, BigDecimal newFreeze,
				Long adminId, Long userId, Long currencyId, String remark){
		LockWalletUpdateLogModel log = new LockWalletUpdateLogModel();
		log.setCreateTime(new Date());
		log.setUpdateTime(new Date());
		log.setUserId(userId);
		log.setCurrencyId(currencyId);

		log.setOldLock(oldLock);
		log.setModifyLock(modifyLock);
		log.setNewLock(newLock);

		log.setOldFreeze(oldFreeze);
		log.setModifyFreeze(modifyFreeze);
		log.setNewFreeze(newFreeze);

		log.setAdminId(adminId);
		log.setRemark(remark);
		this.insert(log);
	}

	@Override
	protected void doMode(LockWalletUpdateLogModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}
}
