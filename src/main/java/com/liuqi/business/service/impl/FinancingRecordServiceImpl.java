package com.liuqi.business.service.impl;


import com.liuqi.base.BaseConstant;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.enums.FinancingConfigGrantTypeEnum;
import com.liuqi.business.enums.FinancingRecordStatusEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.mapper.FinancingRecordMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FinancingRecordServiceImpl extends BaseServiceImpl<FinancingRecordModel,FinancingRecordModelDto> implements FinancingRecordService{

	@Autowired
	private FinancingRecordMapper financingRecordMapper;
	@Autowired
	private FinancingConfigService financingConfigService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private UserWalletLogService userWalletLogService;
	@Override
	public BaseMapper<FinancingRecordModel,FinancingRecordModelDto> getBaseMapper() {
		return this.financingRecordMapper;
	}

	@Override
	protected void doMode(FinancingRecordModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		dto.setFinancingCurrencyName(currencyService.getNameById(dto.getFinancingCurrencyId()));
	}

	@Override
	public List<FinancingRecordModelDto> getByConfigAndUserId(Long configId, Long userId) {
		List<FinancingRecordModelDto> list = new ArrayList<>();
		if (configId != null && configId > 0 && userId != null && userId > 0) {
			FinancingRecordModelDto search=new FinancingRecordModelDto();
			search.setUserId(userId);
			search.setConfigId(configId);
			list = this.queryListByDto(search,true);
		}
		return list;
	}

	@Override
	@Transactional
	public void apply(FinancingConfigModel config, BigDecimal quantity, Long userId) {
		//1判断用户余额
		//扣除可用
		BigDecimal changeUsing= MathUtil.zeroSub(quantity);
		BigDecimal changeFreeze=BigDecimal.ZERO;
		UserWalletModel wallet =userWalletService.modifyWallet(userId, config.getCurrencyId(),changeUsing,changeFreeze);


		//2判断是否超出最大值
		config.setCurQuantity(MathUtil.add(config.getCurQuantity(), quantity));
		if (config.getCurQuantity().compareTo(config.getQuantity()) > 0) {
			throw new BusinessException("已超出最大融资数量");
		}
		financingConfigService.update(config);

		//3计算获取的融资币=换算比例*数量
		BigDecimal grantQuantity = MathUtil.mul(config.getExchange(), quantity);
		//4是否直接获取融资币
		boolean grant = config.getGrantType().equals(FinancingConfigGrantTypeEnum.CUR.getCode());

		//5新增
		FinancingRecordModel record = new FinancingRecordModel();
		record.setConfigId(config.getId());
		record.setCurrencyId(config.getCurrencyId());
		record.setFinancingCurrencyId(config.getFinancingCurrencyId());
		record.setQuantity(quantity);
		//直接获取的 状态为已发放
		record.setStatus(grant ? FinancingRecordStatusEnum.GRANT.getCode() : FinancingRecordStatusEnum.NOTGRANT.getCode());
		record.setUserId(userId);
		record.setGrantQuantity(grantQuantity);
		this.insert(record);
		userWalletLogService.addLog(userId, config.getCurrencyId(), changeUsing, WalletLogTypeEnum.FINANCING.getCode(),  record.getId() , "参与融资融币扣除", wallet);

		//6添加融资币到钱包
		if (grant) {
			this.addToWallet(record, userId, false,false,0L);
		}
	}

	@Override
	@Transactional
	public void addToWallet(Long recordId, Long userId, boolean checkUser,boolean checkStatus,Long adminId) {
		FinancingRecordModel record=this.getById(recordId);
		this.addToWallet(record,userId,checkUser,checkStatus,adminId);
	}

	@Override
	@Transactional
	public void addToWallet(FinancingRecordModel record, Long userId, boolean checkUser,boolean checkStatus,Long adminId) {
		if (checkStatus && !record.getStatus().equals(FinancingRecordStatusEnum.NOTGRANT.getCode())) {
			throw new BusinessException("操作异常，状态异常");
		}
		//判断操作用户是否是传入的用户
		if (checkUser && !record.getUserId().equals(userId)) {
			throw new BusinessException("操作异常，非所有者操作");
		}

		BigDecimal changeUsing= record.getGrantQuantity();
		BigDecimal changeFreeze=BigDecimal.ZERO;
		UserWalletModel wallet =userWalletService.modifyWallet(record.getUserId(), record.getFinancingCurrencyId(),changeUsing,changeFreeze);

		userWalletLogService.addLog(record.getUserId(), record.getFinancingCurrencyId(), record.getGrantQuantity(), WalletLogTypeEnum.FINANCING.getCode(),  record.getId() , "融资融币获取",  wallet);

		record.setStatus(FinancingRecordStatusEnum.GRANT.getCode());
		this.update(record);

		if(adminId!=null && adminId>0){
			loggerService.insert(LoggerModelDto.TYPE_UPDATE,"融资融币"+record.getId()+"手动发放奖励","融资融币",adminId);
		}
	}

	@Override
	public BigDecimal getConfigQuantity(Long configId) {
		return financingRecordMapper.getConfigQuantity(configId);
	}

	/**
	 * 领取
	 * @param config
	 * @param userId
	 */
	@Override
	@Transactional
	public void give(FinancingConfigModel config, Long userId) {

	}
}
