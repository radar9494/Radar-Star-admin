package com.liuqi.business.service.impl;


import com.liuqi.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.PledgeWalletLogModel;
import com.liuqi.business.model.PledgeWalletLogModelDto;


import com.liuqi.business.service.PledgeWalletLogService;
import com.liuqi.business.mapper.PledgeWalletLogMapper;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class PledgeWalletLogServiceImpl extends BaseServiceImpl<PledgeWalletLogModel,PledgeWalletLogModelDto> implements PledgeWalletLogService{


	@Transactional
	@Override
	public void addLog(Long userId, BigDecimal quantity, Integer code, BigDecimal using) {
		PledgeWalletLogModel  log=new PledgeWalletLogModel();
		log.setUserId(userId);
		log.setMoney(quantity);
		log.setType(code);
		log.setBalance(using);
		this.insert(log);
	}

	@Autowired
	private UserService userService;

	@Override
	protected void doMode(PledgeWalletLogModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
	}

	@Autowired
	private PledgeWalletLogMapper pledgeWalletLogMapper;
	

	@Override
	public BaseMapper<PledgeWalletLogModel,PledgeWalletLogModelDto> getBaseMapper() {
		return this.pledgeWalletLogMapper;
	}

}
