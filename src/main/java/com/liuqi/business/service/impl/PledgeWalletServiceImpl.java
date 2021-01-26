package com.liuqi.business.service.impl;


import com.liuqi.business.enums.PledgeWalletLogTypeEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.PledgeWalletMapper;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class PledgeWalletServiceImpl extends BaseServiceImpl<PledgeWalletModel,PledgeWalletModelDto> implements PledgeWalletService{

	@Override
	protected void doMode(PledgeWalletModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
	}

	@Autowired
	private UserService userService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private PledgeWalletLogService pledgeWalletLogService;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private UserWalletLogService userWalletLogService;
	@Autowired
	private OtcApplyConfigService otcApplyConfigService;

	@Transactional
	@Override
	public PledgeWalletModel modifyWalletUsing(Long userId, BigDecimal quantity) {
		if (userId != null &&  userId > 0 ) {
			PledgeWalletModelDto dto = this.getByUserId(userId);
			boolean status = pledgeWalletMapper.modifyWalletUsing(userId, quantity) > 0;
			if (status) {
				dto = this.getByUserId(userId);
			} else {
				System.out.println("u:" + userId);
				throw new BusinessException("可用余额不足");
			}
			return dto;
		}
		throw new BusinessException("参数异常u:" + userId );
	}

	@Override
	public PledgeWalletModel getTotal(  Integer status) {
		return pledgeWalletMapper.getTotal(status);
	}

	@Transactional
	@Override
	public void addPledge(Long userId, BigDecimal quantity) {
		 if(quantity.compareTo(BigDecimal.ZERO)<=0){
		 	throw new BusinessException("数量不正确!");
		 }
		UserModel user=userService.getById(userId);
		 if(user.getOtc()==0){
			 throw new BusinessException("请先申请承兑商!");
		 }

		 Long rdtId=currencyService.getRdtId();
		UserWalletModelDto wallet = userWalletService.getByUserAndCurrencyId(userId, rdtId);
		if(wallet.getUsing().compareTo(quantity)<0){
			throw new BusinessException("余额不足!");
		}
		PledgeWalletModel pleWallet=this.getByUserId(userId);
		OtcApplyConfigModel otcApplyConfigModel=otcApplyConfigService.getConfig();
		if(quantity.add(pleWallet.getUsing()).compareTo(otcApplyConfigModel.getQuantity())>0){
			throw new BusinessException("最多质押"+otcApplyConfigModel.getQuantity().subtract(pleWallet.getUsing()));
		}

		UserWalletModelDto userWalletModelDto = userWalletService.modifyWalletUsing(userId, rdtId, quantity.negate());
		userWalletLogService.addLog(userId,rdtId,quantity.negate(),
				WalletLogTypeEnum.OTC_PLEDGE.getCode(),
				 null,
				WalletLogTypeEnum.OTC_PLEDGE.getName(),userWalletModelDto );
		PledgeWalletModel pledgeWalletModel=this.modifyWalletUsing(userId,quantity);
		pledgeWalletLogService.addLog(userId,quantity, PledgeWalletLogTypeEnum.PLEDGE.getCode(),pledgeWalletModel.getUsing());
	}




	@Transactional
	@Override
	public PledgeWalletModelDto getByUserId(Long userId) {
		PledgeWalletModelDto model = pledgeWalletMapper.getByUserId(userId);
		if(model==null){
			model=new PledgeWalletModelDto();
			model.setUserId(userId);
			model.setUsing(BigDecimal.ZERO);
			model.setFreeze(BigDecimal.ZERO);
			this.insert(model);
		}
		return model;
	}

	@Autowired
	private PledgeWalletMapper pledgeWalletMapper;
	

	@Override
	public BaseMapper<PledgeWalletModel,PledgeWalletModelDto> getBaseMapper() {
		return this.pledgeWalletMapper;
	}

}
