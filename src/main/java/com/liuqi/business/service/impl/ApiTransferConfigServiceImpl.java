package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.mapper.ApiTransferConfigMapper;
import com.liuqi.business.model.ApiTransferConfigModel;
import com.liuqi.business.model.ApiTransferConfigModelDto;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.service.ApiTransferConfigService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.redis.RedisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class ApiTransferConfigServiceImpl extends BaseServiceImpl<ApiTransferConfigModel, ApiTransferConfigModelDto> implements ApiTransferConfigService {

    @Autowired
    private ApiTransferConfigMapper apiTransferConfigMapper;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private CurrencyService currencyService;

    @Override
    public BaseMapper<ApiTransferConfigModel, ApiTransferConfigModelDto> getBaseMapper() {
        return this.apiTransferConfigMapper;
    }

    @Override
    public boolean canTransfer(String name, Long currencyId, Date date) {
        ApiTransferConfigModel config = this.getByName(name);
        return this.canTransfer(config, currencyId, date);
    }

    @Override
    public boolean canTransfer(ApiTransferConfigModel config, Long currencyId, Date date) {
        if (config != null) {
            //开关是否开启
            boolean onOff = SwitchEnum.isOn(config.getOnOff());
            boolean hasCurrency=config.getCurrencyIds().contains(","+currencyId+",");
            Time cur = Time.valueOf(date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
            return onOff && hasCurrency && config.getStartTime().before(cur) && config.getEndTime().after(cur);
        }
        return false;
    }

    @Override
    public ApiTransferConfigModel getByName(String name) {
        String key = KeyConstant.KEY_APITRANSFER_CONFIG + name;
        ApiTransferConfigModelDto config = redisRepository.getModel(key);
        if (config == null) {
            config = apiTransferConfigMapper.getByName(name);
            if (config != null) {
                redisRepository.set(key, config, 1L, TimeUnit.DAYS);
            }
        }
        return config;
    }

    @Override
    public void cleanCacheByModel(ApiTransferConfigModel config) {
        super.cleanCacheByModel(config);
        String key = KeyConstant.KEY_APITRANSFER_CONFIG + config.getName();
        redisRepository.del(key);
    }

    @Override
    protected void doMode(ApiTransferConfigModelDto dto) {
        super.doMode(dto);
        StringBuffer currencyNames = new StringBuffer();
        if (StringUtils.isNotEmpty(dto.getCurrencyIds())) {
            List<Long> searchList = new ArrayList<>();
            for (String temp : dto.getCurrencyIds().split(",")) {
                if (StringUtils.isNotEmpty(temp)) {
                    searchList.add(Long.valueOf(temp));
                }
            }
            List<CurrencyModelDto> list = currencyService.getByIds(searchList);
            if (list != null) {
                for (CurrencyModelDto model : list) {
                    currencyNames.append(model.getName() + ",");
                }
            }
            dto.setCurrencyNames(currencyNames.toString());
        }
    }
}
