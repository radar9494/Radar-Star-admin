package com.liuqi.business.service.impl;


import com.liuqi.business.model.LoggerModelDto;
import com.liuqi.business.service.LoggerService;
import com.liuqi.response.ReturnResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.ListingApplyModel;
import com.liuqi.business.model.ListingApplyModelDto;


import com.liuqi.business.service.ListingApplyService;
import com.liuqi.business.mapper.ListingApplyMapper;

@Service
@Transactional(readOnly = true)
public class ListingApplyServiceImpl extends BaseServiceImpl<ListingApplyModel,ListingApplyModelDto> implements ListingApplyService{


	@Autowired
	private LoggerService loggerService;

	@Transactional
	@Override
	public ReturnResponse updateStatus(Long id, Integer status, Long adminId) {
		ListingApplyModelDto model = this.getById(id);
		model.setStatus(status);
		this.update(model);
		loggerService.insert(LoggerModelDto.TYPE_UPDATE,"上币审核id:"+id,"上币申请",adminId);
		return ReturnResponse.backSuccess();

	}

	@Autowired
	private ListingApplyMapper listingApplyMapper;
	

	@Override
	public BaseMapper<ListingApplyModel,ListingApplyModelDto> getBaseMapper() {
		return this.listingApplyMapper;
	}

}
