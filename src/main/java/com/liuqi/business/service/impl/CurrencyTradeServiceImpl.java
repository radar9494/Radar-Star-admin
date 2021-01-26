package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.mapper.CurrencyTradeMapper;
import com.liuqi.business.model.CurrencyAreaModelDto;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.CurrencyTradeModel;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.service.CurrencyAreaService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CurrencyTradeServiceImpl extends BaseServiceImpl<CurrencyTradeModel, CurrencyTradeModelDto> implements CurrencyTradeService {

	@Autowired
	private CurrencyTradeMapper currencyTradeMapper;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private CurrencyAreaService currencyAreaService;
	@Autowired
	private CurrencyService currencyService;
	@Override
	public BaseMapper<CurrencyTradeModel, CurrencyTradeModelDto> getBaseMapper() {
		return this.currencyTradeMapper;
	}

	@Override
	public CurrencyTradeModelDto getById(Long id) {
		String key = KeyConstant.KEY_CURRENCYTRADE_ID + id;
		CurrencyTradeModelDto currencyTrade = redisRepository.getModel(key);
		if (currencyTrade == null) {
			currencyTrade = currencyTradeMapper.getById(id);
			if (currencyTrade != null) {
				this.doMode(currencyTrade);
				redisRepository.set(key, currencyTrade, 2L, TimeUnit.DAYS);
			}else{
				redisRepository.set(key, "", 2L, TimeUnit.MINUTES);
			}
		}
		return currencyTrade;
	}

    @Override
    public String getNameById(Long tradeId) {
		CurrencyTradeModelDto dto=this.getById(tradeId);
        return dto!=null?dto.getTradeCurrencyName()+"/"+dto.getCurrencyName():"";
    }

    @Override
	public List<CurrencyTradeModelDto> getTradeInfoByArea(Long areaId) {
		List<CurrencyTradeModelDto> list = redisRepository.getModel(KeyConstant.KEY_CURRENCYTRADE_AREAID + areaId);
		if (list == null) {
			//查询数据库
			CurrencyTradeModelDto search=new CurrencyTradeModelDto();
			search.setAreaId(areaId);
			list = this.queryListByDto(search, true);
			if (list != null && list.size() > 0) {
				redisRepository.set(KeyConstant.KEY_CURRENCYTRADE_AREAID + areaId, list, 1L, TimeUnit.DAYS);
			}else{
				redisRepository.set(KeyConstant.KEY_CURRENCYTRADE_AREAID + areaId, new ArrayList<>(), 1L, TimeUnit.MINUTES);
			}
		}
		return list;
	}

	@Override
	public List<CurrencyTradeModelDto> getCanUseTradeInfoByArea(Long areaId) {
		List<CurrencyTradeModelDto> tradeList=this.getTradeInfoByArea(areaId);
		//过滤出可用的
		return tradeList!=null?tradeList.stream().filter(model->model.getStatus()== CurrencyTradeModelDto.STATUS_START).collect(Collectors.toList()):new ArrayList<>();

	}

	@Override
	public CurrencyTradeModelDto getByCurrencyId(Long currencyId, Long tradeCurrencyId) {
		CurrencyTradeModelDto search=new CurrencyTradeModelDto();
		search.setCurrencyId(currencyId);
		search.setTradeCurrencyId(tradeCurrencyId);
		List<CurrencyTradeModelDto> list = this.queryListByDto(search,true);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public CurrencyTradeModelDto getByCurrencyName(String currencyName, String tradeCurrencyName) {
		Long currencyId = -1L;
		Long tradeCurrencyId = -1L;
		CurrencyModelDto currency = currencyService.getByName(currencyName);
		if (currency != null) {
			currencyId = currency.getId();
		}
		currency = currencyService.getByName(tradeCurrencyName);
		if (currency != null) {
			tradeCurrencyId = currency.getId();
		}
		return this.getByCurrencyId(currencyId, tradeCurrencyId);
	}

	/**
	 * 获取交易币种的所有交易对id
	 * @param currencyId
	 * @return
	 */
	@Override
	public List<Long> getAllRelevanceTradeId(Long currencyId) {
		Set<Long> tradeId1 = currencyTradeMapper.getTradeIdByCurrencyId(currencyId);
		Set<Long> tradeId2 = currencyTradeMapper.getTradeIdByTradeCurrencyId(currencyId);
		Set<Long> tradeId = new HashSet<>();
		if(tradeId1!=null) {
			tradeId.addAll(tradeId1);
		}
		if(tradeId2!=null) {
			tradeId.addAll(tradeId2);
		}
		return new ArrayList<>(tradeId);
	}

	@Override
	public List<Long> getTradeIdListByCurrencyId(Long currencyId) {
		Set<Long> tradeId = currencyTradeMapper.getTradeIdByCurrencyId(currencyId);
		return new ArrayList<>(tradeId);
	}

	@Override
	public List<Long> getTradeIdListByTradeCurrencyId(Long tradeCurrencyId) {
		Set<Long> tradeId = currencyTradeMapper.getTradeIdByTradeCurrencyId(tradeCurrencyId);
		return new ArrayList<>(tradeId);
	}

	@Override
	public void cleanAllCache() {
		//清除交易对id缓存
		List<CurrencyTradeModelDto> list=this.queryListByDto(new CurrencyTradeModelDto() ,false);
		if(list!=null && list.size()>0){
			for(CurrencyTradeModel trade:list){
				String key= KeyConstant.KEY_CURRENCYTRADE_ID+trade.getId();
				redisRepository.del(key);
			}
		}
		//清除交易区域缓存
		List<CurrencyAreaModelDto> areaList=currencyAreaService.queryListByDto(new CurrencyAreaModelDto(),false);
		if(areaList!=null && areaList.size()>0){
			for(CurrencyAreaModelDto area:areaList){
				String key= KeyConstant.KEY_CURRENCYTRADE_AREAID+area.getId();
				redisRepository.del(key);
			}
		}
	}

	@Override
	public void cleanCacheByModel(CurrencyTradeModel model) {
		if (model != null) {
			String key = KeyConstant.KEY_CURRENCYTRADE_ID + model.getId();
			redisRepository.del(key);
			//清除区域缓存
			redisRepository.del(KeyConstant.KEY_CURRENCYTRADE_AREAID + model.getAreaId());
		}
	}

	@Override
	protected void doMode(CurrencyTradeModelDto dto) {
		super.doMode(dto);
		if(dto!=null){
			dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
			dto.setTradeCurrencyName(currencyService.getNameById(dto.getTradeCurrencyId()));
			dto.setAreaName(currencyAreaService.getNameById(dto.getAreaId()));
		}
	}
}
