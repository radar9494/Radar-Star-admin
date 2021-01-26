package com.liuqi.business.service.impl;


import com.liuqi.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.CustomerFeedbackModel;
import com.liuqi.business.model.CustomerFeedbackModelDto;


import com.liuqi.business.service.CustomerFeedbackService;
import com.liuqi.business.mapper.CustomerFeedbackMapper;

@Service
@Transactional(readOnly = true)
public class CustomerFeedbackServiceImpl extends BaseServiceImpl<CustomerFeedbackModel,CustomerFeedbackModelDto> implements CustomerFeedbackService{

	@Autowired
	private CustomerFeedbackMapper customerFeedbackMapper;
	@Autowired
	private UserService userService;


	@Transactional
	@Override
	public void feedback(CustomerFeedbackModel model) {
		model.setStatus(0);
		this.insert(model);
	}

	@Transactional
	@Override
	public void deal(Long id) {
		CustomerFeedbackModel item=this.getById(id);
		item.setStatus(1);
		this.update(item);
	}

	@Override
	public BaseMapper<CustomerFeedbackModel,CustomerFeedbackModelDto> getBaseMapper() {
		return this.customerFeedbackMapper;
	}

	@Override
	protected void doMode(CustomerFeedbackModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
	}
}
