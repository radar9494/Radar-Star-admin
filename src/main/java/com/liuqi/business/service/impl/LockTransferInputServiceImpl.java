package com.liuqi.business.service.impl;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.enums.LockWalletLogTypeEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.mapper.LockTransferInputMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class LockTransferInputServiceImpl extends BaseServiceImpl<LockTransferInputModel,LockTransferInputModelDto> implements LockTransferInputService{

	@Autowired
	private LockTransferInputMapper lockTransferInputMapper;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private UserWalletLogService userWalletLogService;
	@Autowired
	private LockWalletService lockWalletService;
	@Autowired
	private LockWalletLogService lockWalletLogService;
	@Override
	public BaseMapper<LockTransferInputModel,LockTransferInputModelDto> getBaseMapper() {
		return this.lockTransferInputMapper;
	}

	@Override
	protected void doMode(LockTransferInputModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}

	@Override
	@Transactional
	public LockTransferInputModel publish(LockTransferConfigModelDto config, Long userId, BigDecimal applyQuantity) {
		//到账金额 =申请*杠杆
		BigDecimal quantity= MathUtil.mul(applyQuantity,new BigDecimal(config.getLever()));

		LockTransferInputModel input = new LockTransferInputModel();
		input.setCurrencyId(config.getCurrencyId());
		input.setUserId(userId);
		input.setApplyQuantity(applyQuantity);
		input.setQuantity(quantity);
		input.setLever(config.getLever());
		this.insert(input);

		//修改可用
		BigDecimal changeUsing=MathUtil.zeroSub(applyQuantity);
		BigDecimal changeFreeze=BigDecimal.ZERO;
		UserWalletModel wallet=userWalletService.modifyWallet(userId,config.getCurrencyId(),changeUsing,changeFreeze);
		userWalletLogService.addLog(userId,config.getCurrencyId(),changeUsing, WalletLogTypeEnum.LOCK_INPUT.getCode(),input.getId(),"转出到锁仓",wallet);


		//修改锁仓
		changeUsing= quantity;
		changeFreeze=BigDecimal.ZERO;
		LockWalletModel lock=lockWalletService.modifyWallet(userId,config.getCurrencyId(),changeUsing,changeFreeze);
		lockWalletLogService.addLog(userId,config.getCurrencyId(),changeUsing, LockWalletLogTypeEnum.LOCK_INPUT.getCode(),input.getId(),"转入",lock);
		return input;
	}
}
