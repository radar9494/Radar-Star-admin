package com.liuqi.business.service.impl;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.mapper.LockConfigMapper;
import com.liuqi.business.model.LockConfigModel;
import com.liuqi.business.model.LockConfigModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.business.service.LockConfigService;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class LockConfigServiceImpl extends BaseServiceImpl<LockConfigModel,LockConfigModelDto> implements LockConfigService{

	@Autowired
	private LockConfigMapper lockConfigMapper;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private CurrencyTradeService currencyTradeService;
	@Autowired
	private CurrencyService currencyService;
	@Override
	public BaseMapper<LockConfigModel,LockConfigModelDto> getBaseMapper() {
		return this.lockConfigMapper;
	}

	@Override
	public List<Long> getLockCurrencyIdList() {
		List<Long> list=lockConfigMapper.getLockCurrencyIdList();
		return ListUtils.removeDuplicate(list);
	}

	@Override
	public List<Long> getLockTradeIdList() {
		List<Long> list=lockConfigMapper.getLockTradeIdList();
		return ListUtils.removeDuplicate(list);
	}

	@Override
	public List<SelectDto> getLockCurrencyList() {
		List<SelectDto> backList=new ArrayList<>();
		List<Long> list=this.getLockCurrencyIdList();
		for(Long id:list){
			backList.add(new SelectDto(id,currencyService.getNameById(id)));
		}
		return backList;
	}

	@Override
	public List<SelectDto> getLockTradeList() {
		List<SelectDto> backList=new ArrayList<>();
		List<Long> list=this.getLockTradeIdList();
		for(Long id:list){
			backList.add(new SelectDto(id,currencyTradeService.getNameById(id)));
		}
		return backList;
	}

	@Override
	public LockConfigModelDto getByCurrencyId(Long currencyId) {
		String key = KeyConstant.KEY_LOCK_CONFIG_CURRENCYID + currencyId;
		LockConfigModelDto config = redisRepository.getModel(key);
		if (config == null) {
			config = lockConfigMapper.getByCurrencyId(currencyId);
			if (config != null) {
				redisRepository.set(key, config, 1L, TimeUnit.DAYS);
			}
		}
		return config;
	}

	@Override
	public LockConfigModelDto getByTradeId(Long tradeId) {
		String key = KeyConstant.KEY_LOCK_CONFIG_TRADEID + tradeId;
		LockConfigModelDto config = redisRepository.getModel(key);
		if (config == null) {
			config = lockConfigMapper.getByTradeId(tradeId);
			if (config != null) {
				redisRepository.set(key, config, 1L, TimeUnit.DAYS);
			}
		}
		return config;
	}

    @Override
	public boolean canRelease(Long tradeId, Integer tradeType, Date date) {
		LockConfigModelDto config=this.getByTradeId(tradeId);
		return this.canRelease(config, tradeType, date);
	}

	@Override
	public boolean canRelease(LockConfigModel config, Integer tradeType, Date date) {
		if(config!=null){
			//开关是否开启
			boolean onOff = BuySellEnum.BUY.getCode().equals(tradeType) ? SwitchEnum.isOn(config.getBuySwitch()) : SwitchEnum.isOn(config.getSellSwitch());
			Time cur=Time.valueOf(date.getHours()+":"+date.getMinutes()+":"+date.getSeconds());
			return onOff && config.getStartTime().before(cur) && config.getEndTime().after(cur);
		}
		return false;
	}

    @Override
    public boolean isLock(Long tradeId) {
		List<Long> list=this.getLockTradeIdList();
        return list != null && list.contains(tradeId);
    }


    @Override
	public void cleanCacheByModel(LockConfigModel config) {
		super.cleanCacheByModel(config);
		String key = KeyConstant.KEY_LOCK_CONFIG_CURRENCYID + config.getCurrencyId();
		redisRepository.del(key);
		key = KeyConstant.KEY_LOCK_CONFIG_TRADEID + config.getTradeId();
		redisRepository.del(key);
	}

	@Override
	protected void doMode(LockConfigModelDto dto) {
		super.doMode(dto);
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		dto.setTradeName(currencyTradeService.getNameById(dto.getTradeId()));
	}
}
