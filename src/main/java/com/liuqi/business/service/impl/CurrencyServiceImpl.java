package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.dto.RechargeSearchDto;
import com.liuqi.business.enums.UsingEnum;
import com.liuqi.business.mapper.CurrencyMapper;
import com.liuqi.business.model.CurrencyModel;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.LoggerModel;
import com.liuqi.business.service.*;
import com.liuqi.redis.RedisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class CurrencyServiceImpl extends BaseServiceImpl<CurrencyModel, CurrencyModelDto> implements CurrencyService {

	@Autowired
	private CurrencyMapper currencyMapper;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private CurrencyConfigService currencyConfigService;
	@Autowired
	private CurrencyTradeService currencyTradeService;
	@Autowired
	private CurrencyAreaService currencyAreaService;
	@Autowired
	private CurrencyDataService currencyDataService;
	@Autowired
	private ConfigService configService;
	@Override
	public BaseMapper<CurrencyModel, CurrencyModelDto> getBaseMapper() {
		return this.currencyMapper;
	}

	@Override
	public CurrencyModelDto getById(Long id) {
		CurrencyModelDto config = redisRepository.getModel(KeyConstant.KEY_CURRENCY_ID + id);
		if (config == null) {
			config = currencyMapper.getById(id);
			if (config != null) {
				//缓存一小时
				redisRepository.set(KeyConstant.KEY_CURRENCY_ID + id, config, 2L, TimeUnit.DAYS);
			}
		}
		return config;
	}

	@Override
	public void afterAddOperate(CurrencyModel currencyModel) {
		currencyConfigService.currencyConfigAdd(currencyModel.getId());
		//初始化币种信息
		currencyDataService.init(currencyModel.getId());
		//记录日志
		LoggerModel log = super.getLogger();
		if (log != null) {
			loggerService.insert(log);
		}
		//TODO 初始化用户钱包
		//userWalletService.initUserWalletByCurrency(currencyModel.getId());
	}

	@Override
	public void afterUpdateOperate(CurrencyModel currencyModel) {
		LoggerModel log = super.getLogger();
		if (log != null) {
			loggerService.insert(log);
		}
	}


	/**
	 * 根据名称获取（缓存）
	 * @param name
	 * @return
	 */
	@Override
	public CurrencyModelDto getByName(String name) {
		String key=KeyConstant.KEY_CURRENCY_NAME+name;
		CurrencyModelDto model = redisRepository.getModel(key);
		if (model == null) {
			model=currencyMapper.getByName(name);
			if(model!=null){
				redisRepository.set(key, model, 2L, TimeUnit.DAYS);
			}
		}
		return model;
	}

	@Override
	public List<CurrencyModelDto> getAll() {
		return this.queryListByDto(new CurrencyModelDto(),false);
	}

	@Override
	public List<CurrencyModelDto> getUsing() {
		CurrencyModelDto search=new CurrencyModelDto();
		search.setStatus(UsingEnum.USING.getCode());
		return this.queryListByDto(search,false);
	}

	@Override
	public void cleanAllCache() {
		currencyAreaService.cleanAllCache();
		currencyConfigService.cleanAllCache();
		currencyTradeService.cleanAllCache();
		List<CurrencyModelDto> list = this.queryListByDto(new CurrencyModelDto(),false);
		if(list!=null && list.size()>0){
			for(CurrencyModel currency:list){
				this.cleanCacheByModel(currency);
			}
		}
	}

	@Override
	public Integer getPositionById(Long id) {
		Integer position = 0;
		CurrencyModel model=this.getById(id);
		position = model != null ? model.getPosition() : 0;
		model=null;
		return position;
	}

	@Override
	public String getNameById(Long id) {
		String name = "";
		CurrencyModel model = this.getById(id);
		name = model != null ? model.getName() : "";
		model = null;
		return name;
	}

	@Override
	public List<Long> getLikeByName(String currencyName,Integer status) {
		return currencyMapper.getLikeByName(currencyName,status);
	}

	@Override
	public void cleanCacheByModel(CurrencyModel currencyModel) {
		redisRepository.del(KeyConstant.KEY_CURRENCY_ID + currencyModel.getId());
		redisRepository.del(KeyConstant.KEY_CURRENCY_NAME + currencyModel.getName());
		redisRepository.del(KeyConstant.KEY_CURRENCY_PIC_ID + currencyModel.getId());
		redisRepository.del(KeyConstant.KEY_CURRENCY_PIC_NAME + currencyModel.getName());
	}

	@Override
	public Long getUsdtId() {
		return Long.valueOf(configService.queryValueByName(ConfigConstant.CONFIGNAME_USDT));
	}

	@Override
	public Long getBaseId() {
		return Long.valueOf(configService.queryValueByName(ConfigConstant.CONFIGNAME_BASE));
	}

	@Override
	public Long getPTId() {
		return Long.valueOf(configService.queryValueByName(ConfigConstant.CONFIGNAME_PT));
	}

	@Override
	public List<RechargeSearchDto> getRecharge() {
		List<RechargeSearchDto> list=new ArrayList<>();
		List<CurrencyModelDto> currencyList= getAll();
		for(CurrencyModelDto dto:currencyList){
			if(StringUtils.isNotEmpty(dto.getThirdCurrency())){
				list.add(new RechargeSearchDto(dto.getId(),dto.getName(),dto.getProtocol(),dto.getThirdCurrency(),dto.getConfirm()));
			}
			if(StringUtils.isNotEmpty(dto.getThirdCurrency2())){
				list.add(new RechargeSearchDto(dto.getId(),dto.getName(),dto.getProtocol2(),dto.getThirdCurrency2(),dto.getConfirm2()));
			}
		}
		return list;
	}
	@Override
	public String getThirdCurrency(Long currencyId, Integer protocol) {
		CurrencyModel currency=this.getById(currencyId);
		return this.getThirdCurrency(currency,protocol);
	}

	@Transactional
	@Override
	public Long getRdbId() {
		return Long.valueOf(configService.queryValueByName(ConfigConstant.CONFIGNAME_RDB));
	}

	@Transactional
	@Override
	public Long getRdtId() {
		return Long.valueOf(configService.queryValueByName(ConfigConstant.CONFIGNAME_RDT));
	}


	@Override
	public String getThirdCurrency(CurrencyModel currency, Integer protocol) {
		String thirdCurrency="";
		if(currency.getProtocol().equals(protocol)){
			thirdCurrency=currency.getThirdCurrency();
		}else if(currency.getProtocol2().equals(protocol)){
			thirdCurrency=currency.getThirdCurrency2();
		}
		return thirdCurrency;
	}
}
