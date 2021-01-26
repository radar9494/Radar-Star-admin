package com.liuqi.business.service.impl;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.enums.WalletDoEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.mapper.ChargeAwardMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ChargeAwardServiceImpl extends BaseServiceImpl<ChargeAwardModel,ChargeAwardModelDto> implements ChargeAwardService{

	@Autowired
	private ChargeAwardMapper chargeAwardMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private UserLevelService userLevelService;
	@Autowired
	private ChargeAwardConfigService chargeAwardConfigService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private UserWalletLogService userWalletLogService;
	@Autowired
	private TradeRecordService tradeRecordService;
	@Autowired
	private CurrencyTradeService currencyTradeService;
	@Autowired
	private TradeService tradeService;
	@Override
	public BaseMapper<ChargeAwardModel,ChargeAwardModelDto> getBaseMapper() {
		return this.chargeAwardMapper;
	}

	@Override
	protected void doMode(ChargeAwardModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		dto.setSnapChargeCurrencyName(currencyService.getNameById(dto.getSnapChargeCurrency()));
	}

	@Override
	@Transactional
	public void createRecord(Long recordId) {
		TradeRecordModel record = tradeRecordService.getById(recordId);
		if (record != null) {
			ChargeAwardConfigModelDto config = chargeAwardConfigService.getConfig();
			CurrencyTradeModel trade = currencyTradeService.getById(record.getTradeId());
			//买手续费
			if (record.getBuyCharge().compareTo(BigDecimal.ZERO) > 0) {
				this.createRecord(record, config, trade.getTradeCurrencyId(), record.getBuyUserId(), record.getBuyCharge(), record.getBuyTrusteeId());
			}
			if (record.getSellCharge().compareTo(BigDecimal.ZERO) > 0) {
				this.createRecord(record, config, trade.getCurrencyId(), record.getSellUserId(), record.getSellCharge(), record.getSellTrusteeId());
			}
		}
	}

	@Override
	public boolean existRecord(Long orderId, Long recordId) {
		return chargeAwardMapper.existRecord(orderId,recordId)>0;
	}

	/**
	 * @param record     成交记录
	 * @param config     配置
	 * @param currencyId 手续费币种
	 * @param userId     用户
	 * @param charge     手续费数量
	 * @param trusteeId  订单id
	 */
	public void createRecord(TradeRecordModel record, ChargeAwardConfigModelDto config, Long currencyId, Long userId, BigDecimal charge, Long trusteeId) {
		if(this.existRecord(trusteeId,record.getId())){
			return;
		}
		//手续费未0 不分
		if (charge.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}
		//机器人不分
		UserModel temp = userService.getById(userId);
		if (YesNoEnum.YES.getCode().equals(temp.getRobot())) {
			return;
		}
		if (config != null && SwitchEnum.isOn(config.getOnOff()) //开关
				&& config.getAwardLevel() > 0) {//设置层数大于0
			List<String> list = userLevelService.getParent(userId);
			if (list != null && list.size() > 0) {
				List<BigDecimal> rates = config.getAwardInfoList();
				Long awardCurrencyId = config.getAwardCurrency();

				int calCount = config.getAwardLevel();
				if (calCount > list.size()) {
					calCount = list.size();
				}

				//获取手续费价格
				BigDecimal price=tradeService.getPriceByCurrencyId(currencyId);
				//总手续费价格
				BigDecimal totalCharge=MathUtil.mul(charge,price);
				//奖励币种价格
				BigDecimal awardPrice =tradeService.getPriceByCurrencyId(awardCurrencyId);
				if(awardPrice.compareTo(BigDecimal.ZERO)<=0){
					return;
				}
				//手续费的数量
				BigDecimal chargeQuantity=MathUtil.div(totalCharge,awardPrice);

				Long calUSerId = 0L;
				BigDecimal quantity = BigDecimal.ZERO;
				for (int i = 0; i < calCount; i++) {
					calUSerId = Long.valueOf(list.get(i));
					quantity = MathUtil.mul(chargeQuantity, MathUtil.divPercent(rates.get(i)));
					if (quantity.compareTo(BigDecimal.ZERO) > 0) {
						ChargeAwardModel award = new ChargeAwardModel();
						award.setUserId(calUSerId);
						award.setCurrencyId(awardCurrencyId);
						award.setQuantity(quantity);
						award.setStatus(WalletDoEnum.NOT.getCode());
						award.setLevel(i + 1);
						award.setOrderId(trusteeId);
						award.setRecordId(record.getId());
						award.setSnapChargeCurrency(currencyId);
						award.setSnapCharge(charge);
						award.setSnapPrice(price);//
						award.setSnapAwardPrice(awardPrice);//
						award.setRemark("下" + (i + 1) + "级[" + temp.getPhone() + "]交易获取交易手续费");
						this.insert(award);
					}
				}
			}
		}
	}


	@Override
	@Transactional
	public void recordRelease(Long id) {
		ChargeAwardModel charge = this.getById(id);
		if (charge != null && !WalletDoEnum.SUCCESS.getCode().equals(charge.getStatus())) {
			BigDecimal changeUsing = charge.getQuantity();
			BigDecimal changeFreeze = BigDecimal.ZERO;
			UserWalletModel wallet = userWalletService.modifyWallet(charge.getUserId(), charge.getCurrencyId(), changeUsing, changeFreeze);
			userWalletLogService.addLog(charge.getUserId(), charge.getCurrencyId(), changeUsing, WalletLogTypeEnum.CHARGE_AWARD.getCode(), charge.getId() , "手续费分红", wallet);

			charge.setStatus(WalletDoEnum.SUCCESS.getCode());
			this.update(charge);
		}
	}
}
