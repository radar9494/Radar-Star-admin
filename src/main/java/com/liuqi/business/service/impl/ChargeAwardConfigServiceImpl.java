package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.mapper.ChargeAwardConfigMapper;
import com.liuqi.business.model.ChargeAwardConfigModel;
import com.liuqi.business.model.ChargeAwardConfigModelDto;
import com.liuqi.business.service.ChargeAwardConfigService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class ChargeAwardConfigServiceImpl extends BaseServiceImpl<ChargeAwardConfigModel, ChargeAwardConfigModelDto> implements ChargeAwardConfigService {

    @Autowired
    private ChargeAwardConfigMapper chargeAwardConfigMapper;
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public BaseMapper<ChargeAwardConfigModel, ChargeAwardConfigModelDto> getBaseMapper() {
        return this.chargeAwardConfigMapper;
    }

    @Override
    public ChargeAwardConfigModelDto getConfig() {
        String key = KeyConstant.KEY_CHARGEAWARD_CONFIG;
        ChargeAwardConfigModelDto config = redisRepository.getModel(key);
        if (config == null) {
            config = chargeAwardConfigMapper.getById(1L);
            if (config != null) {
                redisRepository.set(key, config, 1L, TimeUnit.DAYS);
            }
        }
        return config;
    }

    @Override
    public void cleanCacheByModel(ChargeAwardConfigModel chargeAwardConfigModel) {
        super.cleanCacheByModel(chargeAwardConfigModel);
        String key = KeyConstant.KEY_CHARGEAWARD_CONFIG;
        redisRepository.del(key);
    }
}
