package com.liuqi.business.service.impl;


import com.liuqi.business.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.OtcConfigModel;
import com.liuqi.business.model.OtcConfigModelDto;


import com.liuqi.business.service.OtcConfigService;
import com.liuqi.business.mapper.OtcConfigMapper;

@Service
@Transactional(readOnly = true)
public class OtcConfigServiceImpl extends BaseServiceImpl<OtcConfigModel,OtcConfigModelDto> implements OtcConfigService{

	@Autowired
	private OtcConfigMapper otcConfigMapper;
	@Autowired
	private CurrencyService currencyService;

	@Override
	public BaseMapper<OtcConfigModel,OtcConfigModelDto> getBaseMapper() {
		return this.otcConfigMapper;
	}

	@Override
	protected void doMode(OtcConfigModelDto dto) {
		super.doMode(dto);
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}

	@Override
	public OtcConfigModelDto getByCurrencyId(Long currencyId) {
		return otcConfigMapper.getByCurrencyId(currencyId);
	}
}
