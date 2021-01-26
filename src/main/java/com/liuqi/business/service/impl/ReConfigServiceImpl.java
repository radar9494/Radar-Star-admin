package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.mapper.ReConfigMapper;
import com.liuqi.business.model.ReConfigModel;
import com.liuqi.business.model.ReConfigModelDto;
import com.liuqi.business.service.ReConfigService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class ReConfigServiceImpl extends BaseServiceImpl<ReConfigModel, ReConfigModelDto> implements ReConfigService {

    @Autowired
    private ReConfigMapper reConfigMapper;
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public BaseMapper<ReConfigModel, ReConfigModelDto> getBaseMapper() {
        return this.reConfigMapper;
    }

    @Override
    public ReConfigModelDto getConfig() {
        ReConfigModelDto config = redisRepository.getModel(KeyConstant.KEY_RE_CONFIG);
        if (config == null) {
            config = this.getById(1L);
            redisRepository.set(KeyConstant.KEY_RE_CONFIG, config, 2L, TimeUnit.DAYS);
        }
        return config;
    }

    @Override
    public void cleanCacheByModel(ReConfigModel reConfigModel) {
        super.cleanCacheByModel(reConfigModel);
        redisRepository.del(KeyConstant.KEY_RE_CONFIG);
    }
}
