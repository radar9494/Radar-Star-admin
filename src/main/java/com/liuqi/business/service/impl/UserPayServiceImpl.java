package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.enums.UserPayStatusEnum;
import com.liuqi.business.mapper.UserPayMapper;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.model.UserPayModel;
import com.liuqi.business.model.UserPayModelDto;
import com.liuqi.business.service.UserPayService;
import com.liuqi.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class UserPayServiceImpl extends BaseServiceImpl<UserPayModel,UserPayModelDto> implements UserPayService{

	@Autowired
	private UserPayMapper userPayMapper;
	@Autowired
	private UserService userService;


	@Override
	public BaseMapper<UserPayModel,UserPayModelDto> getBaseMapper() {
		return this.userPayMapper;
	}

	/**
	 * 用户收款信息
	 * @param userId
	 * @return
	 */
	@Override
	public List<UserPayModelDto> getByUserId(Long userId) {
		UserPayModelDto search=new UserPayModelDto();
		search.setUserId(userId);
 		return this.queryListByDto(search,true);
	}

	@Override
	public UserPayModelDto getByUserId(Long userId, Integer payType) {
		UserPayModelDto search=new UserPayModelDto();
		search.setUserId(userId);
		search.setPayType(payType);
		List<UserPayModelDto> list=this.queryListByDto(search,true);
		return list!=null && list.size()>0?list.get(0):null;
	}

	@Override
	@Transactional(propagation= Propagation.REQUIRES_NEW)
	public void init(Long userId, Integer payType) {
		UserPayModel pay=new UserPayModel();
		pay.setUserId(userId);
		pay.setPayType(payType);
		pay.setStatus(UserPayStatusEnum.NOUSING.getCode());
		this.insert(pay);
	}

	@Override
	protected void doMode(UserPayModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
	}
}
