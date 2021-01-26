package com.liuqi.business.service.impl;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.mapper.SuperNodeChargeMapper;
import com.liuqi.business.model.SuperNodeChargeModel;
import com.liuqi.business.model.SuperNodeChargeModelDto;
import com.liuqi.business.model.SuperNodeConfigModel;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Transactional(readOnly = true)
public class SuperNodeChargeServiceImpl extends BaseServiceImpl<SuperNodeChargeModel,SuperNodeChargeModelDto> implements SuperNodeChargeService{

	@Autowired
	private SuperNodeChargeMapper superNodeChargeMapper;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private SuperNodeConfigService superNodeConfigService;
	@Autowired
	private ServiceChargeService serviceChargeService;
	@Autowired
	private TradeService tradeService;
	@Override
	public BaseMapper<SuperNodeChargeModel,SuperNodeChargeModelDto> getBaseMapper() {
		return this.superNodeChargeMapper;
	}

	@Override
	protected void doMode(SuperNodeChargeModelDto dto) {
		super.doMode(dto);
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}

	@Override
	@Transactional
	public void totalCharge(Date date) {
		SuperNodeConfigModel config = superNodeConfigService.getConfig();
		if (config == null) {
			throw new BusinessException("配置异常");
		}
		SuperNodeChargeModel charge = this.getByDate(date);
		if (charge == null) {
			//总手续费
			BigDecimal total = serviceChargeService.getTotalByDate(date);
			//释放币种价格
			BigDecimal price = tradeService.getPriceByCurrencyId(config.getReleaseCurrencyId());

			//释放数量= 总手续/价格*释放百分比
			BigDecimal quantity = BigDecimal.ZERO;
			if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
				quantity = MathUtil.mul(MathUtil.div(total, price), MathUtil.divPercent(config.getReleaseRate()));
			}
			charge = new SuperNodeChargeModel();
			charge.setStartDate(date);
			charge.setCurrencyId(config.getReleaseCurrencyId());
			charge.setQuantity(quantity);
			charge.setSnapPrice(price);
			charge.setRemark("总手续费" + total.toString());
			this.insert(charge);
		}
	}

	@Override
	public SuperNodeChargeModel getByDate(Date date) {
		return superNodeChargeMapper.getByDate(date);
	}
}
