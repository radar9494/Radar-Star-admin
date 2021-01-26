package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.mapper.AlertsMapper;
import com.liuqi.business.model.AlertsModel;
import com.liuqi.business.model.AlertsModelDto;
import com.liuqi.business.service.AlertsService;
import com.liuqi.utils.DateUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional(readOnly = true)
public class AlertsServiceImpl extends BaseServiceImpl<AlertsModel, AlertsModelDto> implements AlertsService {

	@Autowired
	private AlertsMapper alertsMapper;
	

	@Override
	public BaseMapper<AlertsModel, AlertsModelDto> getBaseMapper() {
		return this.alertsMapper;
	}

	@Override
	public Integer queryPagesByTime() {
		return alertsMapper.queryPagesByTime();
	}




}
