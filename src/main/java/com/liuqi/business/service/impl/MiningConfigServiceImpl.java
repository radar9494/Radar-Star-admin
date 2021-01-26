package com.liuqi.business.service.impl;


import com.liuqi.business.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.MiningConfigModel;
import com.liuqi.business.model.MiningConfigModelDto;


import com.liuqi.business.service.MiningConfigService;
import com.liuqi.business.mapper.MiningConfigMapper;

@Service
@Transactional(readOnly = true)
public class MiningConfigServiceImpl extends BaseServiceImpl<MiningConfigModel,MiningConfigModelDto> implements MiningConfigService{

	@Autowired
	private MiningConfigMapper miningConfigMapper;
	@Autowired
	private CurrencyService currencyService;
	

	@Override
	public BaseMapper<MiningConfigModel,MiningConfigModelDto> getBaseMapper() {
		return this.miningConfigMapper;
	}

	public MiningConfigModel findConfig(Integer type,Long currencyId){
		return miningConfigMapper.findOne(type,currencyId);
	}

	@Override
	protected void doMode(MiningConfigModelDto dto) {
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		dto.setImage(currencyService.getById(dto.getCurrencyId()).getPic());
	}
}
