package com.liuqi.business.service.impl;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.UsingEnum;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.EmailModel;
import com.liuqi.business.model.EmailModelDto;


import com.liuqi.business.service.EmailService;
import com.liuqi.business.mapper.EmailMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class EmailServiceImpl extends BaseServiceImpl<EmailModel,EmailModelDto> implements EmailService{

	@Autowired
	private EmailMapper emailMapper;
	@Autowired
	private RedisRepository redisRepository;

	@Override
	public BaseMapper<EmailModel,EmailModelDto> getBaseMapper() {
		return this.emailMapper;
	}

	@Override
	protected void doMode(EmailModelDto dto) {
		super.doMode(dto);
		dto.setUsing(redisRepository.getInteger(KeyConstant.KEY_EMAIL_USING+ DateTime.now().toString("yyyy-MM-dd") +"_"+dto.getAccessKeyId()));
	}

	@Override
	public void cleanAllCache() {
		super.cleanAllCache();
		redisRepository.del(KeyConstant.KEY_EMAIL_LIST);
	}

	@Override
	public void cleanCacheByModel(EmailModel emailModel) {
		super.cleanCacheByModel(emailModel);
	}

	@Override
	public List<EmailModelDto> getUsingList() {
		List<EmailModelDto> list=redisRepository.getModel(KeyConstant.KEY_EMAIL_LIST);
		if(list==null){
			EmailModelDto search=new EmailModelDto();
			search.setStatus(UsingEnum.USING.getCode());
			list=emailMapper.queryList(search);
			if(list!=null){
				redisRepository.set(KeyConstant.KEY_EMAIL_LIST,list,2, TimeUnit.DAYS);
			}
		}
		return list;
	}

	@Override
    public EmailModelDto getCanUsing() {
		List<EmailModelDto> list=this.getUsingList();
		int count=list.size();
		EmailModelDto email=null;
		String curKey=KeyConstant.KEY_EMAIL_CUR;
		Integer cur=redisRepository.getInteger(curKey);
		for(int i=0;i<count;i++){
			if(cur==null||cur>count-1){
				cur=0;
			}
			email=list.get(cur);
			String timeKey=KeyConstant.KEY_EMAIL_USING+ DateTime.now().toString("yyyy-MM-dd") +"_"+email.getAccessKeyId();
			Integer times=redisRepository.getInteger(timeKey);
			if(times==null){
				times=0;
			}
			cur++;
			if(email.getCount()>times){
				times++;
				redisRepository.set(curKey,cur);
				redisRepository.set(timeKey,times,1L,TimeUnit.DAYS);
				break;
			}

		}
		return email;
    }
}
