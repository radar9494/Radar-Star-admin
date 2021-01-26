package com.liuqi.business.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.PermissionModel;
import com.liuqi.business.model.PermissionModelDto;

import com.liuqi.business.service.PermissionService;
import com.liuqi.business.mapper.PermissionMapper;

@Service
@Transactional(readOnly = true)
public class PermissionServiceImpl extends BaseServiceImpl<PermissionModel,PermissionModelDto> implements PermissionService{

	@Autowired
	private PermissionMapper permissionMapper;
	

	@Override
	public BaseMapper<PermissionModel,PermissionModelDto> getBaseMapper() {
		return this.permissionMapper;
	}
	

}
