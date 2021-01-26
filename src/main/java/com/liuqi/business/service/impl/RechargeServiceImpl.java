package com.liuqi.business.service.impl;


import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.dto.CurrencyCountDto;
import com.liuqi.business.enums.*;
import com.liuqi.business.mapper.RechargeMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.utils.DateTimeUtils;
import com.liuqi.utils.MathUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RechargeServiceImpl extends BaseServiceImpl<RechargeModel, RechargeModelDto> implements RechargeService {
	private static cn.hutool.log.Log log = Log4j2LogFactory.get("recharge");
	@Autowired
	private RechargeMapper rechargeMapper;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private UserWalletLogService userWalletLogService;
	@Autowired
	private UserService userService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private AuthCodeService authCodeService;
	@Autowired
	private CurrencyConfigService currencyConfigService;
	@Autowired
	private LockWalletService lockWalletService;
	@Autowired
	private LockWalletLogService lockWalletLogService;

	@Override
	public BaseMapper<RechargeModel, RechargeModelDto> getBaseMapper() {
		return this.rechargeMapper;
	}


	@Override
	public boolean existRemark(Long currencyId, Long userId, String hash) {
		if (StringUtils.isEmpty(hash)) {
			throw new BusinessException("查询hash为空");
		}
		RechargeModelDto search = new RechargeModelDto();
		search.setHash(hash);
		search.setUserId(userId);
		search.setCurrencyId(currencyId);
		List<RechargeModelDto> list = rechargeMapper.queryList(search);
		return list != null && list.size() > 0;
	}

	@Override
	@Transactional
	public void autoRecharge(Long userId, Long currencyId, BigDecimal quantity, String address, String hash, Date createTime, Integer protocol) {
		CurrencyConfigModel config = currencyConfigService.getByCurrencyId(currencyId);
		RechargeModel rechargeModel = new RechargeModel();
		rechargeModel.setUserId(userId);
		rechargeModel.setCurrencyId(currencyId);
		rechargeModel.setQuantity(quantity);
		rechargeModel.setAddress(address);
		rechargeModel.setStatus(RechargeStatusEnum.STATUS_YES.getCode());
		rechargeModel.setCreateTime(createTime);
		rechargeModel.setDealDate(new Date());
		rechargeModel.setHash(hash);//交易hash
		//EOS不推送汇总
		if (protocol.equals(ProtocolEnum.EOS.getCode())||protocol.equals(ProtocolEnum.XRP.getCode())) {
			rechargeModel.setSendType(RechargeSendTypeEnum.SENDED.getCode()); //推送类型
		} else {
			rechargeModel.setSendType(RechargeSendTypeEnum.NOT.getCode()); //推送类型
		}
		rechargeModel.setType(InnerOuterEnum.OUTER.getCode());//外部内部类型
		rechargeModel.setProtocol(protocol);//协议
		rechargeModel.setWalletType(config.getWalletType());
		this.insert(rechargeModel);

		//可用添加 ，冻结不变
		BigDecimal changeUsing = rechargeModel.getQuantity();
		//可用
		if (WalletTypeEnum.USING.getCode().equals(rechargeModel.getWalletType())) {
			UserWalletModel userWalletModel = userWalletService.modifyWalletUsing(rechargeModel.getUserId(), rechargeModel.getCurrencyId(), changeUsing);
			log.info("====================== 充币订单 =================== 充币成功：订单编号:{}, 用户编号:{}，冲币数量:{}",
					rechargeModel.getId(), rechargeModel.getUserId(), rechargeModel.getQuantity());
			//明细---添加日志
			userWalletLogService.addLog(rechargeModel.getUserId(), rechargeModel.getCurrencyId(), changeUsing, WalletLogTypeEnum.RECHARGE.getCode(), rechargeModel.getId(), "充值", userWalletModel);
		} else {
			LockWalletModel lockWalletModel = lockWalletService.modifyWalletLocking(rechargeModel.getUserId(), rechargeModel.getCurrencyId(), changeUsing);
			log.info("====================== 充币订单 =================== 充币成功：订单编号:{}, 用户编号:{}，冲币数量:{}",
					rechargeModel.getId(), rechargeModel.getUserId(), rechargeModel.getQuantity());
			//明细---添加日志
			lockWalletLogService.addLog(rechargeModel.getUserId(), rechargeModel.getCurrencyId(), changeUsing, LockWalletLogTypeEnum.RECHARGE.getCode(), rechargeModel.getId(), "充值", lockWalletModel);

		}
		//短信提醒
		this.sendSms(rechargeModel);
	}


	/**
	 * 内部充值
	 *
	 * @param userId
	 * @param currencyId
	 * @param quantity
	 * @param address
	 * @param hash
	 * @param createTime
	 */
	@Override
	@Transactional
	public void innerRecharge(Long userId, Long currencyId, BigDecimal quantity, String address, String hash, Date createTime, Integer protocol) {
		CurrencyConfigModel config = currencyConfigService.getByCurrencyId(currencyId);
		RechargeModel rechargeModel = new RechargeModel();
		rechargeModel.setUserId(userId);
		rechargeModel.setCurrencyId(currencyId);
		rechargeModel.setQuantity(quantity);
		rechargeModel.setAddress(address);
		rechargeModel.setStatus(RechargeStatusEnum.STATUS_YES.getCode());
		rechargeModel.setCreateTime(createTime);
		rechargeModel.setDealDate(new Date());
		rechargeModel.setHash(hash);//交易hash
		rechargeModel.setSendType(RechargeSendTypeEnum.SENDED.getCode()); //推送类型
		rechargeModel.setType(InnerOuterEnum.INNER.getCode());//外部内部类型
		rechargeModel.setProtocol(protocol);//协议
		rechargeModel.setWalletType(config.getWalletType());
		this.insert(rechargeModel);

		//可用添加 ，冻结不变
		BigDecimal changeUsing = rechargeModel.getQuantity();
		//可用
		if (WalletTypeEnum.USING.getCode().equals(rechargeModel.getWalletType())) {
			UserWalletModel userWalletModel = userWalletService.modifyWalletUsing(rechargeModel.getUserId(), rechargeModel.getCurrencyId(), changeUsing);
			log.info("====================== 充币订单 =================== 充币成功：订单编号:{}, 用户编号:{}，冲币数量:{}",
					rechargeModel.getId(), rechargeModel.getUserId(), rechargeModel.getQuantity());
			//明细---添加日志
			userWalletLogService.addLog(rechargeModel.getUserId(), rechargeModel.getCurrencyId(), rechargeModel.getQuantity(), WalletLogTypeEnum.RECHARGE.getCode(), rechargeModel.getId(), "充值", userWalletModel);
		} else {
			//可用添加 ，冻结不变
			LockWalletModel lockWalletModel = lockWalletService.modifyWalletLocking(rechargeModel.getUserId(), rechargeModel.getCurrencyId(), changeUsing);
			log.info("====================== 充币订单 =================== 充币成功：订单编号:{}, 用户编号:{}，冲币数量:{}",
					rechargeModel.getId(), rechargeModel.getUserId(), rechargeModel.getQuantity());
			//明细---添加日志
			lockWalletLogService.addLog(rechargeModel.getUserId(), rechargeModel.getCurrencyId(), changeUsing, LockWalletLogTypeEnum.RECHARGE.getCode(), rechargeModel.getId(), "充值", lockWalletModel);

		}
		//短信提醒
		this.sendSms(rechargeModel);
	}

	@Override
	public List<CurrencyCountDto> queryCountByDate(Date date, Long currencyId) {
		return rechargeMapper.queryCountByDate(date, currencyId);
	}

	@Override
	public BigDecimal getTotal(RechargeModelDto rechargeModelDto) {
		return rechargeMapper.getTotal(rechargeModelDto);
	}

	@Override
	protected void doMode(RechargeModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}

	private void sendSms(RechargeModel recharge) {
		String currencyName = currencyService.getNameById(recharge.getCurrencyId());
		String sms = String.format("尊敬的用户您好！恭喜您于%s充值%s成功，充值数量为%s。", DateTimeUtils.currentDateTime(), currencyName, recharge.getQuantity().setScale(4, BigDecimal.ROUND_DOWN));
		authCodeService.sendRechargeExtractSms(recharge.getUserId(), sms, "充值");
	}
}
