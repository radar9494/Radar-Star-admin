package com.liuqi.business.service.impl;


import com.google.common.base.Preconditions;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.UsingEnum;
import com.liuqi.business.mapper.UserApiKeyMapper;
import com.liuqi.business.model.UserApiKeyModel;
import com.liuqi.business.model.UserApiKeyModelDto;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.UserApiKeyService;
import com.liuqi.business.service.UserService;
import com.liuqi.redis.CodeCache;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.HiddenChartUtil;
import com.liuqi.utils.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class UserApiKeyServiceImpl extends BaseServiceImpl<UserApiKeyModel,UserApiKeyModelDto> implements UserApiKeyService{

	@Autowired
	private UserApiKeyMapper userApiKeyMapper;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private CurrencyService currencyService;

	@Override
	public BaseMapper<UserApiKeyModel,UserApiKeyModelDto> getBaseMapper() {
		return this.userApiKeyMapper;
	}

	@Override
	@Transactional
	public ReturnResponse createApi(Long userId,Integer status) {

		UserApiKeyModel api=this.getByUserId(userId);
		if(api!=null){
			return ReturnResponse.backFail("已存在api");
		}
		api=new UserApiKeyModel();
		api.setUserId(userId);
		api.setApiKey(UUID.randomUUID().toString().replaceAll("-",""));
		//加密
		String secretKey=UUID.randomUUID().toString().replaceAll("-","");
		secretKey=SignUtil.getEncrypt(secretKey);
		api.setSecretKey(secretKey);
		api.setStatus(status);
		this.insert(api);
		return ReturnResponse.backSuccess();
	}

	@Override
	@Transactional
	public ReturnResponse startApi(Long userId, String code) {
		UserModel user=userService.getById(userId);
		UserApiKeyModel api=this.getByUserId(userId);
		if(api==null){
			return ReturnResponse.backFail("未申请api,请先申请api");
		}
		if(UsingEnum.NOTUSING.getCode().equals(api.getStatus())){
			api.setStatus(UsingEnum.USING.getCode());
			this.update(api);
			CodeCache.verifyCode(user.getName(), code);
		}
		return ReturnResponse.backSuccess();
	}

	@Override
	@Transactional
	public ReturnResponse stopApi(Long userId) {
		UserApiKeyModel api=this.getByUserId(userId);
		if(api==null){
			return ReturnResponse.backFail("未申请api,请先申请api");
		}
		api.setStatus(UsingEnum.NOTUSING.getCode());
		this.update(api);
		return ReturnResponse.backSuccess();
	}

	@Override
	@Transactional
	public ReturnResponse changeSecretKey(Long userId, String code) {
		UserModel user=userService.getById(userId);
		UserApiKeyModel api=this.getByUserId(userId);
		if(api==null){
			return ReturnResponse.backFail("未申请api,请先申请api");
		}
		//加密
		String secretKey=UUID.randomUUID().toString().replaceAll("-","");
		secretKey=SignUtil.getEncrypt(secretKey);
		api.setSecretKey(secretKey);
		this.update(api);
		CodeCache.verifyCode(user.getName(), code);
		return ReturnResponse.backSuccess();
	}


	@Override
	public UserApiKeyModelDto getByUserId(Long userId) {
		return userApiKeyMapper.getByUserId(userId);
	}

	@Override
	public UserApiKeyModelDto getByApiKey(String apiKey) {
		Preconditions.checkNotNull(apiKey,"apikey异常");
		String key= KeyConstant.KEY_API_KEY+apiKey;
		UserApiKeyModelDto userApi = redisRepository.getModel(key);
		if (userApi == null) {
			userApi=userApiKeyMapper.getByApiKey(apiKey);
			if(userApi!=null){
				redisRepository.set(key, userApi, 10L, TimeUnit.MINUTES);
			}
		}
		return userApi;
	}

	@Override
	public void cleanCacheByModel(UserApiKeyModel userApiKeyModel) {
		super.cleanCacheByModel(userApiKeyModel);
		String key= KeyConstant.KEY_API_KEY+userApiKeyModel.getApiKey();
		redisRepository.del(key);
	}

	@Override
	public ReturnResponse getHiddenApi(long userId) {
		UserApiKeyModelDto api=this.getByUserId(userId);
		String secretKey= SignUtil.getDecode(api.getSecretKey());
		String apiKey=api.getApiKey();
		secretKey= HiddenChartUtil.replaceCardId(secretKey,4,4,0,"*","");
		apiKey= HiddenChartUtil.replaceCardId(apiKey,4,4,0,"*","");
		api.setApiKey(apiKey);
		api.setSecretKey(secretKey);
		return ReturnResponse.backSuccess(api);
	}

	@Override
	protected void doMode(UserApiKeyModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}

	@Override
	public ReturnResponse getApi(long userId, String code) {
		UserModel user=userService.getById(userId);
		CodeCache.verifyCode(user.getName(), code);
		UserApiKeyModelDto api=this.getByUserId(userId);
		String secretKey= SignUtil.getDecode(api.getSecretKey());
		String apiKey=api.getApiKey();
		api.setApiKey(apiKey);
		api.setSecretKey(secretKey);
		return ReturnResponse.backSuccess(api);
	}
}
