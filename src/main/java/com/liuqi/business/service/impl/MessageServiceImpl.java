package com.liuqi.business.service.impl;


import cn.hutool.core.date.DateUtil;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.enums.MessageStatusEnum;
import com.liuqi.business.enums.MessageTypeEnum;
import com.liuqi.business.mapper.MessageMapper;
import com.liuqi.business.model.MessageModel;
import com.liuqi.business.model.MessageModelDto;
import com.liuqi.business.service.MessageService;
import com.liuqi.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional(readOnly = true)
public class MessageServiceImpl extends BaseServiceImpl<MessageModel,MessageModelDto> implements MessageService{

	@Autowired
	private MessageMapper messageMapper;
	@Autowired
	private UserService userService;

	@Override
	public BaseMapper<MessageModel,MessageModelDto> getBaseMapper() {
		return this.messageMapper;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void insertMessage(Long userId, String content) {
		MessageModel model=new MessageModel();
		model.setUserId(userId);
		model.setType(MessageTypeEnum.SYS.getCode());
		model.setContent(content);
		model.setStatus(MessageStatusEnum.NOTREAD.getCode());
		this.insert(model);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void insertMessageError(Long userId, Integer type, String content) {
		MessageModel model = this.getTodayByType(userId, type);
		if (model == null) {
			model = new MessageModel();
			model.setUserId(userId);
			model.setType(type);
			model.setContent(content);
			model.setStatus(MessageStatusEnum.NOTREAD.getCode());
			this.insert(model);
		}
	}

	@Override
	@Transactional
	public void readMessage(Long userId,Long id) {
		MessageModel model=this.getById(id);
		if(model!=null && model.getUserId().equals(userId) && MessageStatusEnum.NOTREAD.getCode().equals(model.getStatus())){
			model.setStatus(MessageStatusEnum.READ.getCode());
			this.update(model);
		}
	}

	@Override
	public Integer getNotReadCount(Long userId) {
		return messageMapper.getNotReadCount(userId);
	}

	@Override
	public MessageModelDto getTodayByType(Long userId, Integer type) {
		Date startDate= DateUtil.beginOfDay(new Date());
		Date endDate=DateUtil.endOfDay(startDate);
		return messageMapper.getTodayByType(userId, type,startDate,endDate);
	}

	@Override
	protected void doMode(MessageModelDto dto) {
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
	}
}
