package com.liuqi.business.service.impl;


import com.liuqi.business.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.OtcApplyConfigModel;
import com.liuqi.business.model.OtcApplyConfigModelDto;


import com.liuqi.business.service.OtcApplyConfigService;
import com.liuqi.business.mapper.OtcApplyConfigMapper;

@Service
@Transactional(readOnly = true)
public class OtcApplyConfigServiceImpl extends BaseServiceImpl<OtcApplyConfigModel,OtcApplyConfigModelDto> implements OtcApplyConfigService{
	@Override
	public OtcApplyConfigModelDto getConfig() {
		OtcApplyConfigModelDto config = otcApplyConfigMapper.getConfig();
		config.setRateCurrencyName(currencyService.getNameById(config.getRateCurrencyId()));
		return config;
	}

	@Autowired
	private OtcApplyConfigMapper otcApplyConfigMapper;
	@Autowired
	private CurrencyService currencyService;

	@Override
	protected void doMode(OtcApplyConfigModelDto dto) {
		super.doMode(dto);
		dto.setRateCurrencyName(currencyService.getNameById(dto.getRateCurrencyId()));
	}

	@Override
	public BaseMapper<OtcApplyConfigModel,OtcApplyConfigModelDto> getBaseMapper() {
		return this.otcApplyConfigMapper;
	}

}
