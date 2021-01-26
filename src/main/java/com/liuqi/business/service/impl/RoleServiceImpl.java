package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.mapper.RoleMapper;
import com.liuqi.business.model.RoleModel;
import com.liuqi.business.model.RoleModelDto;
import com.liuqi.business.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl extends BaseServiceImpl<RoleModel, RoleModelDto> implements RoleService {

	@Autowired
	private RoleMapper roleMapper;
	

	@Override
	public BaseMapper<RoleModel, RoleModelDto> getBaseMapper() {
		return this.roleMapper;
	}
	

}
