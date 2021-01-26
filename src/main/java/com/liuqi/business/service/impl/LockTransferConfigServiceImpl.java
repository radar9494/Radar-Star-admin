package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.mapper.LockTransferConfigMapper;
import com.liuqi.business.model.LockTransferConfigModel;
import com.liuqi.business.model.LockTransferConfigModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.LockTransferConfigService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class LockTransferConfigServiceImpl extends BaseServiceImpl<LockTransferConfigModel, LockTransferConfigModelDto> implements LockTransferConfigService {

    @Autowired
    private LockTransferConfigMapper lockTransferConfigMapper;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public BaseMapper<LockTransferConfigModel, LockTransferConfigModelDto> getBaseMapper() {
        return this.lockTransferConfigMapper;
    }

    @Override
    protected void doMode(LockTransferConfigModelDto dto) {
        super.doMode(dto);
        dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
    }

    @Override
    public LockTransferConfigModelDto getByCurrencyId(Long currencyId) {
        String key = KeyConstant.KEY_LOCK_TRANSFER_CONFIG + currencyId;
        LockTransferConfigModelDto config = redisRepository.getModel(key);
        if (config == null) {
            config = lockTransferConfigMapper.getByCurrencyId(currencyId);
            if (config != null) {
                redisRepository.set(key, config, 1L, TimeUnit.DAYS);
            }
        }
        return config;
    }

    @Override
    public void cleanCacheByModel(LockTransferConfigModel config) {
        super.cleanCacheByModel(config);
        String key = KeyConstant.KEY_LOCK_TRANSFER_CONFIG + config.getCurrencyId();
        redisRepository.del(key);
    }
}
