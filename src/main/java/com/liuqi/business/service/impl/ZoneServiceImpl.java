package com.liuqi.business.service.impl;


import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.mapper.ZoneMapper;
import com.liuqi.business.model.ZoneModel;
import com.liuqi.business.service.ZoneService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ZoneServiceImpl implements ZoneService {

    @Autowired
    private ZoneMapper zoneMapper;
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public List<ZoneModel> getAll() {
        String key = KeyConstant.KEY_ZONE;
        List<ZoneModel> list = redisRepository.getModel(key);
        if (list == null) {
            list = zoneMapper.getAll();
            if (list != null) {
                redisRepository.set(key, list, 1L, TimeUnit.DAYS);
            }
        }
        return list;
    }
}
