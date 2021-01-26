package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.mapper.UserWalletUpdateLogMapper;
import com.liuqi.business.model.CurrencyModel;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.model.UserWalletUpdateLogModel;
import com.liuqi.business.model.UserWalletUpdateLogModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.UserService;
import com.liuqi.business.service.UserWalletUpdateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Transactional(readOnly = true)
public class UserWalletUpdateLogServiceImpl extends BaseServiceImpl<UserWalletUpdateLogModel,UserWalletUpdateLogModelDto> implements UserWalletUpdateLogService{

	@Autowired
	private UserWalletUpdateLogMapper userWalletUpdateLogMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private CurrencyService currencyService;

	@Override
	public BaseMapper<UserWalletUpdateLogModel,UserWalletUpdateLogModelDto> getBaseMapper() {
		return this.userWalletUpdateLogMapper;
	}

	/**
	 * 后台修改钱包添加日志
	 * @param oldUsing
	 * @param newUsing
	 * @param modifyUsing
	 * @param oldFreeze
	 * @param modifyFreeze
	 * @param newFreeze
	 * @param adminId
	 * @param userId
	 * @param currencyId
	 */
	@Override
	@Transactional
	public void insert(BigDecimal oldUsing,BigDecimal modifyUsing, BigDecimal newUsing,
					   BigDecimal oldFreeze, BigDecimal modifyFreeze, BigDecimal newFreeze,
					   Long adminId,Long userId,Long currencyId,String remark,Integer type) {
		UserWalletUpdateLogModel log = new UserWalletUpdateLogModel();
		log.setCreateTime(new Date());
		log.setUpdateTime(new Date());
		log.setUserId(userId);
		log.setCurrencyId(currencyId);

		log.setOldUsing(oldUsing);
		log.setModifyUsing(modifyUsing);
		log.setNewUsing(newUsing);

		log.setOldFreeze(oldFreeze);
		log.setModifyFreeze(modifyFreeze);
		log.setNewFreeze(newFreeze);

		log.setAdminId(adminId);
		log.setRemark(remark);
		log.setType(type);
		this.insert(log);
	}

	@Override
	protected void doMode(UserWalletUpdateLogModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}
}
