package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.mapper.UserAdminLoginMapper;
import com.liuqi.business.model.UserAdminLoginModel;
import com.liuqi.business.model.UserAdminLoginModelDto;
import com.liuqi.business.service.UserAdminLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserAdminLoginServiceImpl extends BaseServiceImpl<UserAdminLoginModel, UserAdminLoginModelDto> implements UserAdminLoginService{

	@Autowired
	private UserAdminLoginMapper userAdminLoginMapper;
	

	@Override
	public BaseMapper<UserAdminLoginModel, UserAdminLoginModelDto> getBaseMapper() {
		return this.userAdminLoginMapper;
	}

	@Override
	@Transactional
	public void addLog(String loginName, String ip, String city, String remark) {
		UserAdminLoginModelDto log=new UserAdminLoginModelDto();
		log.setAdminName(loginName);
		log.setIp(ip);
		log.setCity(city);
		log.setRemark(remark);
		this.insert(log);
	}
}
