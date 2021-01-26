package com.liuqi.business.service.impl;


import com.liuqi.business.model.*;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.service.MiningWalletLogService;
import com.liuqi.business.mapper.MiningWalletLogMapper;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class MiningWalletLogServiceImpl extends BaseServiceImpl<MiningWalletLogModel,MiningWalletLogModelDto> implements MiningWalletLogService{

	@Autowired
	private MiningWalletLogMapper miningWalletLogMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private CurrencyService currencyService;
	

	@Override
	public BaseMapper<MiningWalletLogModel,MiningWalletLogModelDto> getBaseMapper() {
		return this.miningWalletLogMapper;
	}


	@Override
	protected void doMode(MiningWalletLogModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}


	@Transactional
	@Override
	public void addLog(Long userId, Long currencyId, BigDecimal money, Integer type, String remarks, MiningWalletModel wallet) {
		MiningWalletLogModel log = new MiningWalletLogModel();
		log.setCurrencyId(currencyId);
		log.setNum(money);
		log.setUserId(userId);
		log.setType(type);
		log.setRemark(remarks);
		log.setBalance(wallet.getUsing());
		log.setSnapshot("可用："+wallet.getUsing()+",冻结："+wallet.getFreeze());
		this.insert(log);
	}

}
