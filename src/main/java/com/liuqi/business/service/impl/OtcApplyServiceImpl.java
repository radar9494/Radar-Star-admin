package com.liuqi.business.service.impl;


import com.liuqi.business.enums.*;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.response.ReturnResponse;
import org.apache.activemq.broker.UserIDBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.OtcApplyMapper;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OtcApplyServiceImpl extends BaseServiceImpl<OtcApplyModel,OtcApplyModelDto> implements OtcApplyService{


	@Transactional
	@Override
	public void refuse(Long id, Long adminId) {
		OtcApplyModelDto model = this.getById(id);
		model.setStatus(2);
		this.update(model);
		if(model.getType()==0){
			PledgeWalletModelDto wallet = pledgeWalletService.getByUserId(model.getUserId());
			BigDecimal quantity=wallet.getUsing();
			wallet.setUsing(BigDecimal.ZERO);
			pledgeWalletService.update(wallet);
			pledgeWalletLogService.addLog(model.getUserId(),quantity.negate(),PledgeWalletLogTypeEnum.PLEDGE_CANCEL.getCode(),
					wallet.getUsing());
			Long rdtId=currencyService.getRdtId();
			UserWalletModelDto userWalletModelDto = userWalletService.modifyWalletUsing(model.getUserId(), rdtId, quantity);
			userWalletLogService.addLog(model.getUserId(),
					rdtId,quantity,WalletLogTypeEnum.OTC_PLEDGE_CANCEL.getCode(),model.getId(),
					"申请取消",userWalletModelDto);
		}else{


		}
	}

	@Override
	public Integer getOtcApplyStatus(Long userId) {
	UserModel userModel=userService.getById(userId);
		Integer status=userModel.getOtc();
		 if(status==0){
			 OtcApplyModel  model = this.getByUserId(userId,0);
			 if(model!=null){
				if(model.getStatus()==0){
					status=2;
				}else if(model.getStatus()==2){
					status=3;
				}

			 }
		 }
            return status;
	}

	@Transactional
	@Override
	public void pledgeCancel(Long userId) {
		 UserModelDto user=userService.getById(userId);
		  if(user.getOtc()==0){
			  throw new BusinessException("未申请成为承兑商!");
		  }
		OtcApplyModel model= this.getByUserId(userId,1);
		if(model!=null&&model.getStatus()==0){
			throw new BusinessException("已申请");
		}
		model=new OtcApplyModel();
		model.setUserId(userId);
		model.setType(1);
		model.setStatus(0);
		this.insert(model);
	}

	@Transactional
	@Override
	public void audit(Long id, Long adminId) {
		OtcApplyModelDto model = this.getById(id);
		model.setStatus(1);
		this.update(model);
		UserModelDto user = userService.getById(model.getUserId());
		if(model.getType()==0){
			user.setOtc(YesNoEnum.YES.getCode());
		}else{
			user.setOtc(YesNoEnum.NO.getCode());
		    //质押返回  扣除手续费
			PledgeWalletModelDto wallet = pledgeWalletService.getByUserId(model.getUserId());
			BigDecimal quantity=wallet.getUsing();
			wallet.setUsing(BigDecimal.ZERO);
			pledgeWalletService.update(wallet);
			pledgeWalletLogService.addLog(model.getUserId(),quantity.negate(),PledgeWalletLogTypeEnum.PLEDGE_CANCEL.getCode(),
					wallet.getUsing());
			Long rdtId=currencyService.getRdtId();
			UserWalletModelDto userWalletModelDto = userWalletService.modifyWalletUsing(model.getUserId(), rdtId, quantity);
			userWalletLogService.addLog(model.getUserId(),
					rdtId,quantity,WalletLogTypeEnum.OTC_PLEDGE_CANCEL.getCode(),model.getId(),
					WalletLogTypeEnum.OTC_PLEDGE_CANCEL.getName(),userWalletModelDto);

			OtcApplyConfigModel config = otcApplyConfigService.getConfig();
			UserWalletModelDto rateWallet = userWalletService.getByUserAndCurrencyId(model.getUserId(), config.getRateCurrencyId());
			if(rateWallet.getUsing().compareTo(config.getRate())<0){
				throw new BusinessException("余额不足!");
			}
			rateWallet.setUsing(rateWallet.getUsing().subtract(config.getRate()));
			userWalletService.update(rateWallet);
			userWalletLogService.addLog(model.getUserId(),
					config.getRateCurrencyId(),config.getRate().negate(),WalletLogTypeEnum.OTC_PLEDGE_CANCEL_RATE.getCode(),model.getId(),
					WalletLogTypeEnum.OTC_PLEDGE_CANCEL_RATE.getName(),rateWallet);
		}
		userService.update(user);
	}

	@Override
	public OtcApplyModel getByUserId(Long userId,Integer type) {
		return otcApplyMapper.getByUserId(userId,type);
	}

	@Autowired
	private UserService userService;
	@Autowired
	private UserAuthService userAuthService;
	@Autowired
	private UserPayService userPayService;
	@Autowired
	private OtcApplyConfigService otcApplyConfigService;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private UserWalletLogService userWalletLogService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private PledgeWalletService pledgeWalletService;
	@Autowired
	private PledgeWalletLogService pledgeWalletLogService;



	@Override
	protected void doMode(OtcApplyModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
	}

	@Transactional
	@Override
	public void apply(Long userId) {
	   //绑定手机号、实名认证、绑定支付信息
		UserModel user=userService.getById(userId);
		if(user.getPhoneAuth()==0){
			throw new BusinessException("请先绑定手机号");
		}
		UserAuthModel auth=userAuthService.getByUserId(userId);
		if (!UserAuthEnum.SUCCESS.getCode().equals(auth.getAuthStatus())) {
			throw new BusinessException("请先实名认证");
		}
		List<UserPayModelDto> byUserId =    userPayService.getByUserId(userId);
      boolean flag=true;
		for(UserPayModelDto item:byUserId){
      	  if(item.getStatus().equals(UserPayStatusEnum.USING.getCode())){
			  flag=false;
			  break;
		  }
	  }
		if(flag){
			throw new BusinessException("请开启或绑定支付信息");
		}
		OtcApplyModel model= this.getByUserId(userId,0);
		if(model!=null&&model.getStatus()==0){
			throw new BusinessException("已申请");
		}
		model=new OtcApplyModel();
		model.setUserId(userId);
		model.setType(0);
		model.setStatus(0);
		this.insert(model);


		OtcApplyConfigModel config = otcApplyConfigService.getConfig();
        Long rdtId=currencyService.getRdtId();
		UserWalletModelDto wallet = userWalletService.getByUserAndCurrencyId(userId, rdtId);
        if(wallet.getUsing().compareTo(config.getQuantity())<0){
			throw new BusinessException("余额不足!");
		}
		UserWalletModelDto userWalletModelDto = userWalletService.modifyWalletUsing(userId, rdtId, config.getQuantity().negate());
       userWalletLogService.addLog(userId,rdtId,config.getQuantity().negate(),
			   WalletLogTypeEnum.OTC_PLEDGE.getCode(),model.getId(),
			   WalletLogTypeEnum.OTC_PLEDGE.getName(),userWalletModelDto );
		PledgeWalletModel pledgeWalletModel=pledgeWalletService.modifyWalletUsing(userId,config.getQuantity());
		pledgeWalletLogService.addLog(userId,config.getQuantity(),PledgeWalletLogTypeEnum.PLEDGE.getCode(),pledgeWalletModel.getUsing());

	}


//	@Transactional
//	@Override
//	public void audit(Long id) {
//		OtcApplyModel model=new OtcApplyModel();
//		model.setUserId(userId);
//		model.setStatus(0);
//		this.insert(model);
//	}





	@Autowired
	private OtcApplyMapper otcApplyMapper;
	

	@Override
	public BaseMapper<OtcApplyModel,OtcApplyModelDto> getBaseMapper() {
		return this.otcApplyMapper;
	}

}
