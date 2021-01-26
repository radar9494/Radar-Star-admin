package com.liuqi.business.service.impl;


import com.liuqi.business.dto.ChargeDto;
import com.liuqi.business.model.CurrencyModel;
import com.liuqi.business.model.ServiceChargeModelDto;
import com.liuqi.business.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.ServiceChargeDetailModel;
import com.liuqi.business.model.ServiceChargeDetailModelDto;

import com.liuqi.business.service.ServiceChargeDetailService;
import com.liuqi.business.mapper.ServiceChargeDetailMapper;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ServiceChargeDetailServiceImpl extends BaseServiceImpl<ServiceChargeDetailModel,ServiceChargeDetailModelDto> implements ServiceChargeDetailService{

	@Autowired
	private ServiceChargeDetailMapper serviceChargeDetailMapper;
	@Autowired
	private CurrencyService currencyService;


	@Override
	public BaseMapper<ServiceChargeDetailModel,ServiceChargeDetailModelDto> getBaseMapper() {
		return this.serviceChargeDetailMapper;
	}

	@Override
	protected void doMode(ServiceChargeDetailModelDto dto) {
		super.doMode(dto);
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}

    @Override
    public List<ChargeDto> total(Date startTime, Date endTime) {
        return serviceChargeDetailMapper.total(startTime,endTime);
    }
}
