package com.liuqi.business.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.CtcPriceDto;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.mapper.CtcConfigMapper;
import com.liuqi.business.model.CtcConfigModel;
import com.liuqi.business.model.CtcConfigModelDto;
import com.liuqi.business.service.CtcConfigService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.redis.RedisRepository;
import com.liuqi.third.zb.SearchPrice;
import com.liuqi.utils.MathUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class CtcConfigServiceImpl extends BaseServiceImpl<CtcConfigModel,CtcConfigModelDto> implements CtcConfigService{

	@Autowired
	private CtcConfigMapper ctcConfigMapper;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private SearchPrice searchPrice;
	@Override
	public BaseMapper<CtcConfigModel,CtcConfigModelDto> getBaseMapper() {
		return this.ctcConfigMapper;
	}

	@Override
	protected void doMode(CtcConfigModelDto dto) {
		super.doMode(dto);
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}

	@Override
	public CtcConfigModelDto getByCurrencyId(Long currencyId) {
		return ctcConfigMapper.getByCurrencyId(currencyId);
	}

    @Override
    public CtcPriceDto getPrice(CtcConfigModelDto config) {
		CtcPriceDto dto=new CtcPriceDto();
		String key= KeyConstant.KEY_CTC_PRICE+config.getId();
		String value = redisRepository.getString(key);
		if(StringUtils.isNotEmpty(value)) {
			dto= JSONObject.parseObject(value,CtcPriceDto.class);
		}else{
			BigDecimal tempPrice = BigDecimal.ZERO;
			//判断价格是否异常
			if (StringUtils.isNotEmpty(config.getOuterPrice())) {//获取外部价格
				tempPrice = searchPrice.getPrice(config.getOuterPrice());
			} else {
				//价格= 当前价格
				tempPrice = config.getPrice();
			}
			tempPrice = tempPrice != null ? tempPrice : BigDecimal.ZERO;
			//价格+价格*百分比
			BigDecimal buyPrice = MathUtil.add(tempPrice, MathUtil.mul(tempPrice, MathUtil.divPercent(config.getBuyRang())));
			BigDecimal sellPrice = MathUtil.add(tempPrice, MathUtil.mul(tempPrice, MathUtil.divPercent(config.getSellRang())));

			dto.setBuyPrice(buyPrice);
			dto.setSellPrice(sellPrice);
			redisRepository.set(key, JSONObject.toJSONString(dto), 3L, TimeUnit.MINUTES);
		}
		return dto;
    }

	@Override
	public boolean canPublish(Long currencyId, Integer tradeType, Date date) {
		CtcConfigModel config = this.getByCurrencyId(currencyId);
		return this.canPublish(config, tradeType, date);
	}

	@Override
	public boolean canPublish(CtcConfigModel config, Integer tradeType, Date date) {
		if (config != null) {
			//开关是否开启
			boolean onOff = BuySellEnum.BUY.getCode().equals(tradeType) ? SwitchEnum.isOn(config.getBuySwitch()) : SwitchEnum.isOn(config.getSellSwitch());
			Time cur = Time.valueOf(date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
			return onOff && config.getStartTime().before(cur) && config.getEndTime().after(cur);
		}
		return false;
	}

    @Override
    public List<CtcConfigModelDto> getAll() {
		CtcConfigModelDto search=new CtcConfigModelDto();
        return this.queryListByDto(search,true);
    }

    @Override
	public void cleanCacheByModel(CtcConfigModel config) {
		String key= KeyConstant.KEY_CTC_PRICE+config.getId();
		redisRepository.del(key);
	}
}
