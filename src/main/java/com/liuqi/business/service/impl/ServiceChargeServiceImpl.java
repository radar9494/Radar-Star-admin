package com.liuqi.business.service.impl;


import cn.hutool.core.date.DateUtil;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.dto.ChargeDto;
import com.liuqi.business.mapper.ServiceChargeMapper;
import com.liuqi.business.model.ServiceChargeModel;
import com.liuqi.business.model.ServiceChargeModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.ServiceChargeDetailService;
import com.liuqi.business.service.ServiceChargeService;
import com.liuqi.business.service.TradeService;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ServiceChargeServiceImpl extends BaseServiceImpl<ServiceChargeModel,ServiceChargeModelDto> implements ServiceChargeService{

	@Autowired
	private ServiceChargeMapper serviceChargeMapper;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private ServiceChargeDetailService serviceChargeDetailService;
	@Autowired
	private TradeService tradeService;
	@Override
	public BaseMapper<ServiceChargeModel,ServiceChargeModelDto> getBaseMapper() {
		return this.serviceChargeMapper;
	}

	@Override
	@Transactional
	public void totalCharge(Date date) {
		Date startTime = DateUtil.beginOfDay(date);
		Date endTime = DateUtil.endOfDay(startTime);
		List<ChargeDto> list = serviceChargeDetailService.total(startTime, endTime);
		if(list!=null && list.size()>0){
			for(ChargeDto dto:list){
				ServiceChargeModel charge=this.getByDateAndCurrency(date,dto.getCurrencyId());
				if(charge==null) {
					charge=new ServiceChargeModel();
					charge.setCalcDate(date);
					charge.setCharge(dto.getCharge());
					charge.setCurrencyId(dto.getCurrencyId());
					charge.setSnapPrice(tradeService.getPriceByCurrencyId(dto.getCurrencyId()));
					this.insert(charge);
				}
			}
		}
	}

	@Override
	public List<ServiceChargeModelDto> getByDate(Date date) {
		return serviceChargeMapper.getByDate(date);
	}

	@Override
	public ServiceChargeModelDto getByDateAndCurrency(Date date, Long currencyId) {
		return serviceChargeMapper.getByDateAndCurrency(date,currencyId);
	}

    @Override
    public BigDecimal getTotalByDate(Date date) {
		BigDecimal total=BigDecimal.ZERO;
		List<ServiceChargeModelDto> list=this.getByDate(date);
		if(list!=null){
			for(ServiceChargeModelDto dto:list){
				total= MathUtil.add(total,MathUtil.mul(dto.getCharge(),dto.getSnapPrice()));
			}
		}
		return total;
    }

    @Override
	protected void doMode(ServiceChargeModelDto dto) {
		super.doMode(dto);
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}
}
