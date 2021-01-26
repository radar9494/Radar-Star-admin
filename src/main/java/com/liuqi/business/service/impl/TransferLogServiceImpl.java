package com.liuqi.business.service.impl;


import com.liuqi.business.enums.MiningWalletLogEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.TransferLogMapper;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class TransferLogServiceImpl extends BaseServiceImpl<TransferLogModel,TransferLogModelDto> implements TransferLogService{

	@Autowired
	private MiningWalletService miningWalletService;
	@Autowired
	private OtcWalletService otcWalletService;
	@Autowired
	private UserWalletService userWalletService;

	@Autowired
	private MiningWalletLogService miningWalletLogService;
	@Autowired
	private OtcWalletLogService otcWalletLogService;
	@Autowired
	private UserWalletLogService userWalletLogService;
    @Autowired
	private CurrencyService currencyService;

	@Override
	protected void doMode(TransferLogModelDto dto) {
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		//0 币币到OTC 1币币到矿池 2 OTC到币币 3 矿池到币币
		super.doMode(dto);

	}

	@Transactional
	@Override
	public void transfer(Long userId, Long currencyId, Integer type, BigDecimal quantity) {
         if(quantity.compareTo(BigDecimal.ZERO)<=0){
         	throw new BusinessException("数量不正确!");
		 }
		TransferLogModel model=new TransferLogModel();
         model.setUserId(userId);
         model.setCurrencyId(currencyId);
         model.setType(type);
         model.setMoney(quantity);
         this.insert(model);
		//0 币币到OTC 1币币到矿池
		if(type==0){
			UserWalletModel wallet=userWalletService.getByUserAndCurrencyId(userId, currencyId);
			if(wallet.getUsing().compareTo(quantity)<0){
				throw new BusinessException("币币余额不足");
			}
			wallet.setUsing(wallet.getUsing().subtract(quantity));
			userWalletService.update(wallet);
			userWalletLogService.addLog(userId,currencyId,quantity.negate(), WalletLogTypeEnum.INNER_TRANSFER.getCode(),model.getId(),"币币到OTC",wallet);
			OtcWalletModel otcWalletModel=otcWalletService.getByUserAndCurrencyId(userId, currencyId);
			otcWalletModel.setUsing(otcWalletModel.getUsing().add(quantity));
			otcWalletService.update(otcWalletModel);
		    otcWalletLogService.addLog(userId,currencyId,quantity,WalletLogTypeEnum.INNER_TRANSFER.getCode(),model.getId(),"币币到OTC",otcWalletModel);
		}
		else if(type==1){
			UserWalletModel wallet=userWalletService.getByUserAndCurrencyId(userId, currencyId);
			if(wallet.getUsing().compareTo(quantity)<0){
				throw new BusinessException("币币余额不足");
			}
			wallet.setUsing(wallet.getUsing().subtract(quantity));
			userWalletService.update(wallet);
			userWalletLogService.addLog(userId,currencyId,quantity.negate(), WalletLogTypeEnum.INNER_TRANSFER.getCode(),model.getId(),"币币到矿池",wallet);
			MiningWalletModel miningWalletModel=miningWalletService.findByUserIdAndCurrencyId(userId, currencyId);
			miningWalletModel.setUsing(miningWalletModel.getUsing().add(quantity));
			miningWalletService.update(miningWalletModel);
			miningWalletLogService.addLog(currencyId,userId,quantity, MiningWalletLogEnum.INNER_TRANSFER.getCode(),"币币到矿池",miningWalletModel);
		}
		//2 OTC到币币 3 矿池到币币
		else if(type==2){
			OtcWalletModel otcWalletModel=otcWalletService.getByUserAndCurrencyId(userId, currencyId);
			if(otcWalletModel.getUsing().compareTo(quantity)<0){
				throw new BusinessException("OTC余额不足");
			}
			otcWalletModel.setUsing(otcWalletModel.getUsing().subtract(quantity));
			otcWalletService.update(otcWalletModel);
			otcWalletLogService.addLog(userId,currencyId,quantity.negate(),WalletLogTypeEnum.INNER_TRANSFER.getCode(),model.getId(),"OTC到币币",otcWalletModel);

			UserWalletModel wallet=userWalletService.getByUserAndCurrencyId(userId, currencyId);
			if(wallet.getGatewaySwitch()==0){
				throw new BusinessException("网关未开启");
			}
			wallet.setUsing(wallet.getUsing().add(quantity));
			userWalletService.update(wallet);
			userWalletLogService.addLog(userId,currencyId,quantity, WalletLogTypeEnum.INNER_TRANSFER.getCode(),model.getId(),"OTC到币币",wallet);
		}else if(type==3){
			MiningWalletModel miningWalletModel=miningWalletService.findByUserIdAndCurrencyId(userId, currencyId);
			if(miningWalletModel.getUsing().compareTo(quantity)<0){
				throw new BusinessException("矿池余额不足");
			}

			miningWalletModel.setUsing(miningWalletModel.getUsing().subtract(quantity));
			miningWalletService.update(miningWalletModel);
			miningWalletLogService.addLog(currencyId,userId,quantity.negate(),WalletLogTypeEnum.INNER_TRANSFER.getCode(),"矿池到币币",miningWalletModel);

			UserWalletModel wallet=userWalletService.getByUserAndCurrencyId(userId, currencyId);
			wallet.setUsing(wallet.getUsing().add(quantity));
			userWalletService.update(wallet);
			userWalletLogService.addLog(userId,currencyId,quantity, WalletLogTypeEnum.INNER_TRANSFER.getCode(),model.getId(),"矿池到币币",wallet);
		}

	}

	@Autowired
	private TransferLogMapper transferLogMapper;
	

	@Override
	public BaseMapper<TransferLogModel,TransferLogModelDto> getBaseMapper() {
		return this.transferLogMapper;
	}

}
