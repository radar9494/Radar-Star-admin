package com.liuqi.business.service.impl;


import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.FinancingIntroduceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.FinancingConfigModel;
import com.liuqi.business.model.FinancingConfigModelDto;


import com.liuqi.business.service.FinancingConfigService;
import com.liuqi.business.mapper.FinancingConfigMapper;

@Service
@Transactional(readOnly = true)
public class FinancingConfigServiceImpl extends BaseServiceImpl<FinancingConfigModel,FinancingConfigModelDto> implements FinancingConfigService{

	@Autowired
	private FinancingConfigMapper financingConfigMapper;
	@Autowired
	private FinancingIntroduceService financingIntroduceService;
	@Autowired
	private CurrencyService currencyService;

	@Override
	public BaseMapper<FinancingConfigModel,FinancingConfigModelDto> getBaseMapper() {
		return this.financingConfigMapper;
	}


	@Override
	public void afterAddOperate(FinancingConfigModel financingConfigModel) {
		super.afterAddOperate(financingConfigModel);
		//初始化介绍信息
		financingIntroduceService.init(financingConfigModel.getId());
	}

	@Override
	protected void doMode(FinancingConfigModelDto dto) {
		super.doMode(dto);
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		dto.setFinancingCurrencyName(currencyService.getNameById(dto.getFinancingCurrencyId()));
	}
}
