package com.liuqi.business.service.impl;

import cn.hutool.core.date.DateUtil;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.enums.LockWalletLogTypeEnum;
import com.liuqi.business.mapper.LockTransferOutputMapper;
import com.liuqi.business.model.LockTransferConfigModelDto;
import com.liuqi.business.model.LockTransferOutputModel;
import com.liuqi.business.model.LockTransferOutputModelDto;
import com.liuqi.business.model.LockWalletModel;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Transactional(readOnly = true)
public class LockTransferOutputServiceImpl extends BaseServiceImpl<LockTransferOutputModel,LockTransferOutputModelDto> implements LockTransferOutputService{

	@Autowired
	private LockTransferOutputMapper lockTransferOutputMapper;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private UserService userService;
	@Autowired
	private LockWalletService lockWalletService;
	@Autowired
	private LockWalletLogService lockWalletLogService;
	@Override
	public BaseMapper<LockTransferOutputModel,LockTransferOutputModelDto> getBaseMapper() {
		return this.lockTransferOutputMapper;
	}

	@Override
	protected void doMode(LockTransferOutputModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		dto.setReceiveUserName(userService.getNameById(dto.getReceiveUserId()));
		dto.setReceiveRealName(userService.getRealNameById(dto.getReceiveUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}

	@Override
	@Transactional
	public LockTransferOutputModel publish(LockTransferConfigModelDto config, Long userId, Long receiveUserId, BigDecimal applyQuantity) {
		//判断进入转入次数
		int times = this.getTodayTimes(config.getCurrencyId(), userId);
		if (times >= config.getTransferTimes()) {
			throw new BusinessException("已达进入转出次数" + config.getTransferTimes());
		}


		//手续费 =申请数量*手续费
		BigDecimal change = MathUtil.mul(applyQuantity, MathUtil.divPercent(config.getTransferRate()));
		BigDecimal quantity = MathUtil.sub(applyQuantity, change);

		LockTransferOutputModel output = new LockTransferOutputModel();
		output.setCurrencyId(config.getCurrencyId());
		output.setUserId(userId);
		output.setApplyQuantity(applyQuantity);
		output.setQuantity(quantity);
		output.setRate(config.getTransferRate());
		output.setCharge(change);
		output.setReceiveUserId(receiveUserId);
		this.insert(output);

		//申请用户扣减
		BigDecimal changeUsing = MathUtil.zeroSub(applyQuantity);
		BigDecimal changeFreeze = BigDecimal.ZERO;
		LockWalletModel lock = lockWalletService.modifyWallet(userId, config.getCurrencyId(), changeUsing, changeFreeze);
		lockWalletLogService.addLog(userId, config.getCurrencyId(), changeUsing, LockWalletLogTypeEnum.LOCK_OUTPUT.getCode(),  output.getId() , "转出", lock);


		//接受用户添加
		changeUsing = quantity;
		changeFreeze = BigDecimal.ZERO;
		lock = lockWalletService.modifyWallet(receiveUserId, config.getCurrencyId(), changeUsing, changeFreeze);
		lockWalletLogService.addLog(receiveUserId, config.getCurrencyId(), changeUsing, LockWalletLogTypeEnum.LOCK_INPUT.getCode(),  output.getId() , "转入", lock);
		return output;
	}

	@Override
	public int getTodayTimes(Long currencyId, Long userId) {
		Date startTime = DateUtil.beginOfDay(new Date());
		Date endTime = DateUtil.endOfDay(startTime);
		return this.getTodayTimes(currencyId, userId, startTime, endTime);
	}

	@Override
	public int getTodayTimes(Long currencyId, Long userId, Date startTime, Date endTime) {
		return lockTransferOutputMapper.getTodayTimes(currencyId, userId, startTime, endTime);
	}
}
