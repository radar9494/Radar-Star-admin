package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.UserAuthEnum;
import com.liuqi.business.mapper.UserAuthMapper;
import com.liuqi.business.model.UserAuthModel;
import com.liuqi.business.model.UserAuthModelDto;
import com.liuqi.business.service.UserAuthService;
import com.liuqi.exception.BusinessException;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class UserAuthServiceImpl extends BaseServiceImpl<UserAuthModel,UserAuthModelDto> implements UserAuthService{

	@Autowired
	private UserAuthMapper userAuthMapper;
	@Autowired
	private RedisRepository redisRepository;

	@Override
	public BaseMapper<UserAuthModel,UserAuthModelDto> getBaseMapper() {
		return this.userAuthMapper;
	}


	@Override
	public UserAuthModelDto getByUserId(Long userId) {
		String key=KeyConstant.KEY_USER_AUTHINFO+userId;
		UserAuthModelDto dto = redisRepository.getModel(key);
		if (dto == null) {
			dto=userAuthMapper.getByUserId(userId);
			if (dto!=null) {
				//缓存2天
				redisRepository.set(key, dto, 2L, TimeUnit.DAYS);
			}
		}
		return dto;
	}

	@Override
	public String getNameByUserId(Long id) {
		String name = "";
		UserAuthModel auth = this.getByUserId(id);
		name = auth != null ? auth.getRealName() : "";
		auth = null;
		return name;
	}


	@Override
	public boolean auth(Long userId) {
		UserAuthModelDto auth=this.getByUserId(userId);
		return UserAuthEnum.SUCCESS.getCode().equals(auth.getAuthStatus());
	}

	@Override
	public void cleanCacheByModel(UserAuthModel userAuthModel) {
		String key=KeyConstant.KEY_USER_AUTHINFO+userAuthModel.getUserId();
		redisRepository.del(key);
	}

	@Override
	@Transactional
	public void initAuth(Long userId) {
		UserAuthModel auth=new UserAuthModel();
		auth.setUserId(userId);
		auth.setAuthStatus(UserAuthEnum.NO.getCode());
		this.insert(auth);
	}

	@Override
	@Transactional
	public void authUser(Long userId, Integer status,String remark,Long adminId) {
		UserAuthModel auth=this.getByUserId(userId);
		//判断状态
		if(UserAuthEnum.SUCCESS.getCode().equals(status)||UserAuthEnum.FAIL.getCode().equals(status)){
			Integer count=this.getSuccessIdcart(auth.getIdcart());
			if(UserAuthEnum.SUCCESS.getCode().equals(status) && count>=1) {
				throw new BusinessException("该身份证已认证！");
			}
			auth.setAuthStatus(status);
			auth.setRemark(remark);
			this.update(auth);
		}
	}

	@Override
	public Integer getSuccessIdcart(String idcart) {
		return userAuthMapper.getSuccessIdcart(idcart);
	}

	@Override
	@Transactional
	public void approveOne(Long userId, UserAuthModel userAuthModel) {
		UserAuthModel auth=this.getByUserId(userId);
		int count=this.getSuccessIdcart(userAuthModel.getIdcart());
		if(count>0){
			throw new BusinessException("身份证已认证");
		}
		auth.setIdcart(userAuthModel.getIdcart());
		auth.setRealName(userAuthModel.getRealName());
		this.update(auth);
	}

	@Transactional
	@Override
	public void init(Long id) {
		UserAuthModelDto dto = this.getByUserId(id);
		if(dto==null){
				dto=new UserAuthModelDto();
				dto.setUserId(id);
				dto.setAuthStatus(0);
				this.insert(dto);
		}
	}

	@Override
	@Transactional
	public void approveTwo(UserAuthModel auth) {
		int count=this.getSuccessIdcart(auth.getIdcart());
		if(count>0){
			throw new BusinessException("身份证已认证");
		}
		auth.setAuthStatus(UserAuthEnum.DOING.getCode());
		this.update(auth);
	}
}
