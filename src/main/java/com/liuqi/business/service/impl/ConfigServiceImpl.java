package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.mapper.ConfigMapper;
import com.liuqi.business.model.ConfigModel;
import com.liuqi.business.model.ConfigModelDto;
import com.liuqi.business.service.ConfigService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class ConfigServiceImpl extends BaseServiceImpl<ConfigModel, ConfigModelDto> implements ConfigService {

	@Autowired
	private ConfigMapper configMapper;
	@Autowired
	private RedisRepository redisRepository;

	@Override
	public BaseMapper<ConfigModel, ConfigModelDto> getBaseMapper() {
		return this.configMapper;
	}

	//配置缓存
	@Override
	public String queryValueByName(String s){
		ConfigModel config=this.queryByName(s);
		return config!=null?config.getVal():"";
	}
	//配置缓存
	@Override
	public ConfigModelDto queryByName(String name) {
		String key= KeyConstant.KEY_CONFIG_NAME+name;
		ConfigModelDto config = redisRepository.getModel(key);
		if (config == null) {
			config=configMapper.queryByName(name);
			if(config!=null){
				redisRepository.set(key, config, 1L, TimeUnit.DAYS);
			}
		}
		return config;
	}


	@Override
	public void cleanAllCache() {
		List<ConfigModelDto> list = this.queryListByDto(new ConfigModelDto(),false);
		if(list!=null && list.size()>0){
			for(ConfigModel config:list){
				this.cleanCacheByModel(config);
			}
		}
	}

	@Override
	public void cleanCacheByModel(ConfigModel configModel) {
		if(configModel!=null) {
			redisRepository.del(KeyConstant.KEY_CONFIG_NAME + configModel.getName());
		}
	}

	@Override
	public String getProjectAddress() {
		return queryValueByName(ConfigConstant.CONFIG_ADDRESS);
	}
	@Override
	public String getProjectName() {
		return queryValueByName(ConfigConstant.CONFIG_PROJECTNAME);
	}
}
