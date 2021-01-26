package com.liuqi.business.service.impl;


import com.liuqi.business.model.OtcWalletModel;
import com.liuqi.business.model.UserWalletLogModel;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.OtcWalletLogModel;
import com.liuqi.business.model.OtcWalletLogModelDto;


import com.liuqi.business.service.OtcWalletLogService;
import com.liuqi.business.mapper.OtcWalletLogMapper;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class OtcWalletLogServiceImpl extends BaseServiceImpl<OtcWalletLogModel,OtcWalletLogModelDto> implements OtcWalletLogService{

	@Autowired
	private OtcWalletLogMapper otcWalletLogMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private CurrencyService currencyService;


	@Override
	protected void doMode(OtcWalletLogModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getUserId()));
	}

	@Transactional
	@Override
	public void addLog(Long userId, Long currencyId, BigDecimal money, Integer type, Long id, String remarks, OtcWalletModel wallet) {
		OtcWalletLogModel log = new OtcWalletLogModel();
		log.setCurrencyId(currencyId);
		log.setMoney(money);
		log.setUserId(userId);
		log.setType(type);
		log.setRemark(remarks);
		log.setOrderId(id);
		log.setBalance(wallet.getUsing());
		log.setSnapshot("可用："+wallet.getUsing()+",冻结："+wallet.getFreeze());
		this.insert(log);
	}

	@Override
	public BaseMapper<OtcWalletLogModel,OtcWalletLogModelDto> getBaseMapper() {
		return this.otcWalletLogMapper;
	}

}
