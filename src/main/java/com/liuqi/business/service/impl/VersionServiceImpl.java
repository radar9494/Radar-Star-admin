package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.mapper.VersionMapper;
import com.liuqi.business.model.VersionModel;
import com.liuqi.business.model.VersionModelDto;
import com.liuqi.business.service.VersionService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class VersionServiceImpl extends BaseServiceImpl<VersionModel, VersionModelDto> implements VersionService {

    @Autowired
    private VersionMapper versionMapper;
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public BaseMapper<VersionModel, VersionModelDto> getBaseMapper() {
        return this.versionMapper;
    }

    @Override
    public VersionModelDto getConfig() {
        VersionModelDto config = redisRepository.getModel(KeyConstant.KEY_VERSION_CONFIG);
        if (config == null) {
            config = versionMapper.getById(1L);
            if (config != null) {
                //缓存一小时
                redisRepository.set(KeyConstant.KEY_VERSION_CONFIG, config, 2L, TimeUnit.DAYS);
            }
        }
        return config;
    }

    @Override
    public void cleanCacheByModel(VersionModel versionModel) {
        super.cleanCacheByModel(versionModel);
        redisRepository.del(KeyConstant.KEY_VERSION_CONFIG);
    }
}
