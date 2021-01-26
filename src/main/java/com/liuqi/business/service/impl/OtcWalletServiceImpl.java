package com.liuqi.business.service.impl;


import com.liuqi.business.enums.UsingEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.utils.MathUtil;
import org.apache.commons.compress.utils.Lists;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.OtcWalletMapper;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OtcWalletServiceImpl extends BaseServiceImpl<OtcWalletModel,OtcWalletModelDto> implements OtcWalletService{

	@Autowired
	private OtcWalletMapper otcWalletMapper;
	@Autowired
	private OtcConfigService otcConfigService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserWalletUpdateLogService userWalletUpdateLogService;
	@Autowired
	private OtcWalletLogService otcWalletLogService;
	

	@Override
	public BaseMapper<OtcWalletModel,OtcWalletModelDto> getBaseMapper() {
		return this.otcWalletMapper;
	}

	@Transactional
	@Override
	public OtcWalletModelDto getByUserAndCurrencyId(Long userId, Long currencyId) {
		OtcWalletModelDto wallet= otcWalletMapper.getByUserAndCurrencyId(userId,currencyId);
		//插入时判断  如果没有钱包新建一个
		if(wallet==null){
			wallet= ((OtcWalletService) AopContext.currentProxy()).addWallet(userId, currencyId);
		}
		return wallet;
	}



	@Override
	protected void doMode(OtcWalletModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}


	@Override
	public List<WalletStaticModel> groupByCurrencyId() {
		return otcWalletMapper.groupByCurrencyId();
	}

	@Override
	public OtcWalletModel getTotal(Long id,Integer status) {
		return otcWalletMapper.getTotal(id,status);
	}

	@Transactional
	@Override
	public void insertOtcWallet(Long userId) {
		//初始化用户钱包
		List<OtcConfigModelDto> list = otcConfigService.queryListByDto(null, false);
		 for(OtcConfigModelDto config:list){
			 //获取钱包  没有时创建
		 this.getByUserAndCurrencyId(userId,config.getCurrencyId());
		 }
	}

	@Transactional
	@Override
	public void adminUpdate(OtcWalletModel wallet, Long opeId) {
		//获取原对象
		OtcWalletModel model = this.getById(wallet.getId());
		BigDecimal oldUsing = model.getUsing();
		BigDecimal oldFreeze  = model.getFreeze();
		BigDecimal modifyUsing=wallet.getUsing();
		BigDecimal modifyFreeze=wallet.getFreeze();
		BigDecimal newUsing = MathUtil.add(oldUsing,modifyUsing);
		BigDecimal newFreeze = MathUtil.add(oldFreeze,modifyFreeze);
		model.setUsing(newUsing);
		model.setFreeze(newFreeze);
		model.setUpdateTime(new Date());
		//更新数据
		this.modifyWallet(model.getUserId(),model.getCurrencyId(),modifyUsing,modifyFreeze);
		userWalletUpdateLogService.insert(oldUsing,modifyUsing,newUsing,oldFreeze,modifyFreeze,newFreeze,opeId,model.getUserId(),model.getCurrencyId(),wallet.getRemark(),1);
		otcWalletLogService.addLog(model.getUserId(), model.getCurrencyId(), modifyUsing, WalletLogTypeEnum.SYS.getCode(), 0L,"系统修改", wallet);
	}

	@Override
	public List getByUserId(Long userId, String currencyName) {
		OtcWalletModelDto search=new OtcWalletModelDto();
		search.setUserId(userId);
	  if(StringUtils.isEmpty(currencyName)){
		  List<Long> currencyList= Lists.newArrayList();
		  List<OtcConfigModelDto> configList = otcConfigService.queryListByDto(null,false);
		  for(OtcConfigModelDto config:configList){
			  currencyList.add(config.getCurrencyId());
		  }
		  if(currencyList==null || currencyList.size()==0){
			  currencyList.add(-1L);
		  }
		  search.setCurrencyList(currencyList);
	  }else{
		  CurrencyModelDto currency = currencyService.getByName(currencyName);
		  Long currencyId =currency!=null?currency.getId():0L;
		  search.setCurrencyId(currencyId);
	  }

		return this.queryListByDto(search,true);
	}

	@Transactional
	@Override
	public OtcWalletModelDto modifyWallet(Long userId, Long currencyId, BigDecimal changeUsing, BigDecimal changeFreeze) {
		if (userId != null && currencyId != null && userId > 0 && currencyId > 0) {
			OtcWalletModelDto dto = this.getByUserAndCurrencyId(userId, currencyId);
			boolean status = otcWalletMapper.modifyWallet(userId, currencyId, changeUsing, changeFreeze) > 0;
			if (status) {
				dto = this.getByUserAndCurrencyId(userId, currencyId);
			} else {
				System.out.println("u:" + userId + ",c:" + currencyId);
				throw new BusinessException("Otc钱包余额不足");
			}
			return dto;
		}
		throw new BusinessException("参数异常u:" + userId + ",c:" + currencyId);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public OtcWalletModelDto addWallet(Long userId, Long currencyId) {
		if(userId!=null && currencyId!=null && userId>0 && currencyId>0){
			OtcWalletModelDto wallet = new OtcWalletModelDto();
			wallet.setUserId(userId);
			wallet.setFreeze(BigDecimal.ZERO);
			wallet.setUsing(BigDecimal.ZERO);
			wallet.setCurrencyId(currencyId);
			this.insert(wallet);
			return wallet;
		}
		return null;
	}
}
