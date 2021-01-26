package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.mapper.UserWalletAddressMapper;
import com.liuqi.business.model.CurrencyModel;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.model.UserWalletAddressModel;
import com.liuqi.business.model.UserWalletAddressModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.UserService;
import com.liuqi.business.service.UserWalletAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional(readOnly = true)
public class UserWalletAddressServiceImpl extends BaseServiceImpl<UserWalletAddressModel,UserWalletAddressModelDto> implements UserWalletAddressService{

	@Autowired
	private UserWalletAddressMapper userWalletAddressMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private CurrencyService currencyService;

	@Override
	public BaseMapper<UserWalletAddressModel,UserWalletAddressModelDto> getBaseMapper() {
		return this.userWalletAddressMapper;
	}

	@Override
	@Transactional
	public void addAddress(String remark, Long currencyId, String address,String memo, Long userId) {
		UserWalletAddressModel addressModel = new UserWalletAddressModel();
		addressModel.setUserId(userId);
		addressModel.setAddress(address);
		addressModel.setCurrencyId(currencyId);
		addressModel.setRemark(remark);
		addressModel.setMemo(memo);
		addressModel.setCreateTime(new Date());
		addressModel.setUpdateTime(new Date());
		userWalletAddressMapper.insert(addressModel);
	}

	/**
	 * 删除提币地址
	 * @param id
	 * @return
	 */
	@Override
	@Transactional
	public boolean delete(Long id) {
		return userWalletAddressMapper.removeById(id)>0;
	}


	@Override
	protected void doMode(UserWalletAddressModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}
}
