package com.liuqi.business.service.impl;

import cn.hutool.core.date.DateUtil;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.dto.TransferTotalDto;
import com.liuqi.business.enums.*;
import com.liuqi.business.mapper.ApiTransferMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.external.api.dto.TransferDto;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Transactional(readOnly = true)
public class ApiTransferServiceImpl extends BaseServiceImpl<ApiTransferModel,ApiTransferModelDto> implements ApiTransferService{

	@Autowired
	private ApiTransferMapper apiTransferMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private UserWalletLogService userWalletLogService;
	@Autowired
	private LockWalletService lockWalletService;
	@Autowired
	private LockWalletLogService lockWalletLogService;
	@Override
	public BaseMapper<ApiTransferModel,ApiTransferModelDto> getBaseMapper() {
		return this.apiTransferMapper;
	}

	@Override
	@Transactional
	public Long publish(Long userId, Long currencyId, TransferDto transferDto, ApiTransferConfigModel config) {
		ApiTransferModel transfer = this.getByNameAndNum(transferDto.getName(), transferDto.getNum());
		if (transfer == null) {
			//判断
			TransferTotalDto total = this.getTodayByUser(transferDto.getName(), userId, currencyId);
			if (total != null) {
				int time = total.getTimes() != null ? total.getTimes() : 0;
				BigDecimal quantity = total.getTotal() != null ? total.getTotal() : BigDecimal.ZERO;
				if (total.getTimes() >= config.getDayTimes()) {
					throw new BusinessException("超出每日限制次数：" + config.getDayTimes());
				}
				BigDecimal temp = MathUtil.add(quantity, transferDto.getQuantity());
				if (temp.compareTo(config.getDayMaxQuantity()) > 0) {
					throw new BusinessException("超出每日限制数量：" + config.getDayMaxQuantity());
				}
			}
			transfer = new ApiTransferModel();
			transfer.setUserId(userId);
			transfer.setCurrencyId(currencyId);
			transfer.setQuantity(transferDto.getQuantity());
			transfer.setStatus(0);
			transfer.setType(transferDto.getType());
			transfer.setName(transferDto.getName());
			transfer.setNum(transferDto.getNum());
			transfer.setTransferName(transferDto.getTransferName());
			this.insert(transfer);

			//自动审核
			if(SwitchEnum.isOn(config.getAutoConfirm())){
				this.agree(transfer.getId(),"自动审核");
			}
		}
		return transfer.getId();
	}

	@Override
	public ApiTransferModel getByNameAndNum(String name, String num) {
		return apiTransferMapper.getByNameAndNum(name, num);
	}

	@Override
	public TransferTotalDto getTodayByUser(String name, Long userId, Long currencyId) {
		Date startDate = DateUtil.beginOfDay(new Date());
		Date endDate = DateUtil.endOfDay(startDate);
		return apiTransferMapper.getByUser(name, userId, currencyId, startDate, endDate);
	}

	@Override
	public TransferTotalDto getByUser(String name, Long userId, Long currencyId, Date startDate, Date endDate) {
		return apiTransferMapper.getByUser(name, userId, currencyId, startDate, endDate);
	}

	@Override
	@Transactional
	public void agree(Long id,String remark) {
		ApiTransferModel transfer = this.getById(id);
		if (transfer != null && WalletDoEnum.NOT.getCode().equals(transfer.getStatus())) {
			BigDecimal changeUsing=transfer.getQuantity();
			BigDecimal changeFreeze=BigDecimal.ZERO;
			//可用
			if(ApiTransferTypeEnum.USING.getCode().equals(transfer.getType())){
				UserWalletModel wallet=userWalletService.modifyWallet(transfer.getUserId(),transfer.getCurrencyId(),changeUsing,changeFreeze);
				userWalletLogService.addLog(transfer.getUserId(),transfer.getCurrencyId(),changeUsing, WalletLogTypeEnum.OUT_TRANSFER.getCode(),transfer.getId(),"外部转入",wallet);
			}else{//锁仓
				LockWalletModel wallet = lockWalletService.modifyWallet(transfer.getUserId(), transfer.getCurrencyId(), changeUsing, changeFreeze);
				lockWalletLogService.addLog(transfer.getUserId(), transfer.getCurrencyId(), changeUsing, LockWalletLogTypeEnum.OUT_TRANSFER.getCode(),  transfer.getId() , "外部转入", wallet);
			}
			transfer.setRemark(remark);
			transfer.setStatus(WalletDoEnum.SUCCESS.getCode());
			this.update(transfer);
		}
	}

	@Override
	@Transactional
	public void refuse(Long id,String remark) {
		ApiTransferModel transfer = this.getById(id);
		if (transfer != null && WalletDoEnum.NOT.getCode().equals(transfer.getStatus())) {
			transfer.setStatus(WalletDoEnum.FAIL.getCode());
			transfer.setRemark(remark);
			this.update(transfer);
		}
	}

	@Override
	protected void doMode(ApiTransferModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}
}
