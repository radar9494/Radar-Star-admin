package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.mapper.OtcOrderRecordLogMapper;
import com.liuqi.business.model.OtcOrderRecordLogModel;
import com.liuqi.business.model.OtcOrderRecordLogModelDto;
import com.liuqi.business.service.OtcOrderRecordLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class OtcOrderRecordLogServiceImpl extends BaseServiceImpl<OtcOrderRecordLogModel,OtcOrderRecordLogModelDto> implements OtcOrderRecordLogService{

	@Autowired
	private OtcOrderRecordLogMapper otcOrderRecordLogMapper;
	

	@Override
	public BaseMapper<OtcOrderRecordLogModel,OtcOrderRecordLogModelDto> getBaseMapper() {
		return this.otcOrderRecordLogMapper;
	}

	@Override
	@Transactional
	public void addLog(Long recordId, String opeName, String remark) {
		OtcOrderRecordLogModel log = new OtcOrderRecordLogModel();
		log.setName(opeName);
		log.setRecordId(recordId);
		log.setRemark(remark);
		this.insert(log);
	}

	@Override
	public List<OtcOrderRecordLogModelDto> getByRecordId(Long recordId) {
		OtcOrderRecordLogModelDto search=new OtcOrderRecordLogModelDto();
		search.setRecordId(recordId);
		return this.queryListByDto(search,true);
	}
}
