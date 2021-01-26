package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.mapper.SmsConfigMapper;
import com.liuqi.business.model.SmsConfigModel;
import com.liuqi.business.model.SmsConfigModelDto;
import com.liuqi.business.service.SmsConfigService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class SmsConfigServiceImpl extends BaseServiceImpl<SmsConfigModel, SmsConfigModelDto> implements SmsConfigService {

    @Autowired
    private SmsConfigMapper smsConfigMapper;
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public BaseMapper<SmsConfigModel, SmsConfigModelDto> getBaseMapper() {
        return this.smsConfigMapper;
    }

    @Override
    public SmsConfigModelDto getConfig() {
        String key = KeyConstant.KEY_SMS_CONFIG;
        SmsConfigModelDto config = redisRepository.getModel(key);
        if (config == null) {
            config = smsConfigMapper.getById(1L);
            if (config != null) {
                redisRepository.set(key, config, 1L, TimeUnit.DAYS);
            }
        }
        return config;
    }

    @Override
    public String getSign() {
        SmsConfigModelDto config=this.getConfig();
        return config.getSign();
    }

    @Override
    public String getKey() {
        SmsConfigModelDto config=this.getConfig();
        return config.getKey();
    }

    @Override
    public String getSecret() {
        SmsConfigModelDto config=this.getConfig();
        return config.getSecret();
    }

    @Override
    public String getGjKey() {
        SmsConfigModelDto config=this.getConfig();
        return config.getGjkey();
    }

    @Override
    public void cleanCacheByModel(SmsConfigModel smsConfigModel) {
        super.cleanCacheByModel(smsConfigModel);
        String key = KeyConstant.KEY_SMS_CONFIG;
        redisRepository.del(key);
    }
}
