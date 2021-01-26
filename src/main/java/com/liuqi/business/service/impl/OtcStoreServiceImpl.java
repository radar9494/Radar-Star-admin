package com.liuqi.business.service.impl;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.enums.OtcStoreTypeEnum;
import com.liuqi.business.mapper.OtcStoreMapper;
import com.liuqi.business.model.OtcStoreModel;
import com.liuqi.business.model.OtcStoreModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.OtcStoreService;
import com.liuqi.business.service.UserService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OtcStoreServiceImpl extends BaseServiceImpl<OtcStoreModel,OtcStoreModelDto> implements OtcStoreService {

	@Autowired
	private OtcStoreMapper otcStoreMapper;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private UserService userService;

	@Override
	protected void doMode(OtcStoreModelDto dto) {
		super.doMode(dto);
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setOtcName(userService.getOtcNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
	}
	@Override
	public BaseMapper<OtcStoreModel,OtcStoreModelDto> getBaseMapper() {
		return this.otcStoreMapper;
	}

	@Override
	public OtcStoreModelDto getByUserId(Long userId, Long currencyId) {
		OtcStoreModelDto store = otcStoreMapper.getByUserId(userId, currencyId);
		if (store == null) {
			store = ((OtcStoreService) AopContext.currentProxy()).init(userId, currencyId);
		}
		return store;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public OtcStoreModelDto init(Long userId, Long currencyId) {
		OtcStoreModelDto store = new OtcStoreModelDto();
		store.setCurrencyId(currencyId);
		store.setUserId(userId);
		store.setType(OtcStoreTypeEnum.GENERAL.getCode());
		store.setTotal(0);
		store.setSuccess(0);
		this.insert(store);
		return store;
	}

	@Override
	@Transactional
	public void addTotal(Long userId, Long currencyId) {
		OtcStoreModelDto store = this.getByUserId(userId, currencyId);
		store.setTotal(store.getTotal() + 1);
		this.update(store);
	}

	@Override
	@Transactional
	public void addSuccess(Long userId, Long currencyId) {
		OtcStoreModelDto store = this.getByUserId(userId, currencyId);
		store.setSuccess(store.getSuccess() + 1);
		this.update(store);
	}
}
