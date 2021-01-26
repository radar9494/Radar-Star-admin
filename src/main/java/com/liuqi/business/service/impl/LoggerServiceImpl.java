package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.mapper.LoggerMapper;
import com.liuqi.business.model.LoggerModel;
import com.liuqi.business.model.LoggerModelDto;
import com.liuqi.business.service.LoggerService;
import com.liuqi.business.service.UserAdminService;
import com.liuqi.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional(readOnly = true)
public class LoggerServiceImpl extends BaseServiceImpl<LoggerModel, LoggerModelDto> implements LoggerService {

	@Autowired
	private LoggerMapper loggerMapper;
	@Autowired
	private UserAdminService userAdminService;

	@Override
	public BaseMapper<LoggerModel, LoggerModelDto> getBaseMapper() {
		return this.loggerMapper;
	}


	@Override
	@Transactional
	public void insert(LoggerModel log) {
		cleanAllCache();
		log.setCreateTime(new Date());
		log.setUpdateTime(new Date());
		if (log.getVersion() == null) {
			log.setVersion(0);
		}
		loggerMapper.insert(log);
	}

	/**
	 * 新增
	 * @param type  LoggerModel
	 * @param content
	 * @param title
	 * @param operId
	 */
	@Override
	@Transactional
	public void insert(int type, String content, String title, Long operId){
		LoggerModel loggerModel=new LoggerModel();
		loggerModel.setType(type);
		loggerModel.setContent(content);
		loggerModel.setTitle(title);
		loggerModel.setAdminId(operId);
		this.insert(loggerModel);
	}

	@Override
	protected void doMode(LoggerModelDto dto) {
		super.doMode(dto);
		dto.setAdminName(userAdminService.getNameById(dto.getAdminId()));
	}
}
