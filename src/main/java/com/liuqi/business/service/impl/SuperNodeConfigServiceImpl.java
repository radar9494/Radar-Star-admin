package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.mapper.SuperNodeConfigMapper;
import com.liuqi.business.model.SuperNodeConfigModel;
import com.liuqi.business.model.SuperNodeConfigModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.SuperNodeConfigService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class SuperNodeConfigServiceImpl extends BaseServiceImpl<SuperNodeConfigModel, SuperNodeConfigModelDto> implements SuperNodeConfigService {

    @Autowired
    private SuperNodeConfigMapper superNodeConfigMapper;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public BaseMapper<SuperNodeConfigModel, SuperNodeConfigModelDto> getBaseMapper() {
        return this.superNodeConfigMapper;
    }

    @Override
    protected void doMode(SuperNodeConfigModelDto dto) {
        super.doMode(dto);
        dto.setJoinCurrencyName(currencyService.getNameById(dto.getJoinCurrencyId()));
        dto.setReleaseCurrencyName(currencyService.getNameById(dto.getReleaseCurrencyId()));
    }

    //配置缓存
    @Override
    public SuperNodeConfigModelDto getConfig() {
        String key = KeyConstant.KEY_SUPERNODE_CONFIG;
        SuperNodeConfigModelDto config = redisRepository.getModel(key);
        if (config == null) {
            config = superNodeConfigMapper.getById(1L);
            if (config != null) {
                redisRepository.set(key, config, 2L, TimeUnit.DAYS);
            }
        }
        return config;
    }

    @Override
    public void cleanCacheByModel(SuperNodeConfigModel superNodeConfigModel) {
        super.cleanCacheByModel(superNodeConfigModel);
        String key = KeyConstant.KEY_SUPERNODE_CONFIG;
        redisRepository.del(key);
    }
}
