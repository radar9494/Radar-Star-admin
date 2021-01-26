package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.WalletTypeEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.mapper.CurrencyConfigMapper;
import com.liuqi.business.model.CurrencyConfigModel;
import com.liuqi.business.model.CurrencyConfigModelDto;
import com.liuqi.business.service.CurrencyConfigService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.exception.BusinessException;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class CurrencyConfigServiceImpl extends BaseServiceImpl<CurrencyConfigModel, CurrencyConfigModelDto> implements CurrencyConfigService {

	@Autowired
	private CurrencyConfigMapper currencyConfigMapper;
	@Autowired
	private CurrencyTradeService currencyTradeService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private RedisRepository redisRepository;
	@Override
	public BaseMapper<CurrencyConfigModel, CurrencyConfigModelDto> getBaseMapper() {
		return this.currencyConfigMapper;
	}

	@Override
	public CurrencyConfigModelDto getByCurrencyId(Long currencyId) {
		String key= KeyConstant.KEY_CURRENCY_CONFIG_ID+currencyId;
		CurrencyConfigModelDto config = redisRepository.getModel(key);
		if (config == null) {
			config = currencyConfigMapper.getByCurrencyId(currencyId);
			if(config!=null){
				this.doMode(config);
				redisRepository.set(key, config, 2L, TimeUnit.DAYS);
			}
		}
		return config;
	}

	/**
	 * （后台）添加
	 * @param
	 * @param
	 * @return
	 */
	@Override
	@Transactional
	public boolean currencyConfigAdd(Long currencyId) {
		try{
			CurrencyConfigModel currencyConfigModel = new CurrencyConfigModel();
			currencyConfigModel.setCurrencyId(currencyId);
			currencyConfigModel.setExtractRate(BigDecimal.ZERO);
			currencyConfigModel.setExtractMaxDay(BigDecimal.ZERO);
			currencyConfigModel.setExtractMax(BigDecimal.ZERO);
			currencyConfigModel.setExtractMin(BigDecimal.ZERO);
			currencyConfigModel.setExtractMaxDaySwitch(0);
			currencyConfigModel.setExtractSwitch(0);
			currencyConfigModel.setRechargeSwitch(0);
			currencyConfigModel.setPercentage(YesNoEnum.NO.getCode());
			currencyConfigModel.setWalletType(WalletTypeEnum.USING.getCode());
			this.insert(currencyConfigModel);
		}catch(Exception e){
			e.printStackTrace();
			throw new BusinessException("操作异常");
		}
		return true;
	}

	@Override
	public void cleanAllCache() {
		currencyTradeService.cleanAllCache();
		List<CurrencyConfigModelDto> list = this.queryListByDto(new CurrencyConfigModelDto(),false);
		if(list!=null && list.size()>0){
			for(CurrencyConfigModel config:list){
				this.cleanCacheByModel(config);
			}
		}
	}

	@Override
	public void cleanCacheByModel(CurrencyConfigModel currencyConfigModel) {
		redisRepository.del(KeyConstant.KEY_CURRENCY_CONFIG_ID + currencyConfigModel.getCurrencyId());
	}

	@Override
	protected void doMode(CurrencyConfigModelDto dto) {
		super.doMode(dto);
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		dto.setPosition(currencyService.getPositionById(dto.getCurrencyId()));
		dto.setRateCurrencyName(currencyService.getNameById(dto.getRateCurrencyId()));
	}
}
