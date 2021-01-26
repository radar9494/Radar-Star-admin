package com.liuqi.business.service.impl;


import com.liuqi.business.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.TransferConfigModel;
import com.liuqi.business.model.TransferConfigModelDto;


import com.liuqi.business.service.TransferConfigService;
import com.liuqi.business.mapper.TransferConfigMapper;

@Service
@Transactional(readOnly = true)
public class TransferConfigServiceImpl extends BaseServiceImpl<TransferConfigModel,TransferConfigModelDto> implements TransferConfigService{

	@Override
	public TransferConfigModelDto getByCurrencyId(Long currencyId) {
		return transferConfigMapper.getByCurrencyId(currencyId);
	}

	@Autowired
	private TransferConfigMapper transferConfigMapper;
	@Autowired
	private CurrencyService currencyService;

	@Override
	protected void doMode(TransferConfigModelDto dto) {
		super.doMode(dto);
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}

	@Override
	public BaseMapper<TransferConfigModel,TransferConfigModelDto> getBaseMapper() {
		return this.transferConfigMapper;
	}

}
