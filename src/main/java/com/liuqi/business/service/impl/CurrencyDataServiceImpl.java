package com.liuqi.business.service.impl;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.mapper.CurrencyDataMapper;
import com.liuqi.business.model.CurrencyDataModel;
import com.liuqi.business.model.CurrencyDataModelDto;
import com.liuqi.business.service.CurrencyDataService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class CurrencyDataServiceImpl extends BaseServiceImpl<CurrencyDataModel, CurrencyDataModelDto> implements CurrencyDataService {

    @Autowired
    private CurrencyDataMapper currencyDataMapper;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public BaseMapper<CurrencyDataModel, CurrencyDataModelDto> getBaseMapper() {
        return this.currencyDataMapper;
    }

    @Override
    protected void doMode(CurrencyDataModelDto dto) {
        super.doMode(dto);
        dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
    }

    @Override
    public CurrencyDataModelDto getById(Long id) {
        String key = KeyConstant.KEY_CURRENCY_DATA_ID + id;
        CurrencyDataModelDto config = redisRepository.getModel(key);
        if (config == null) {
            config = currencyDataMapper.getById(id);
            if (config != null) {
                this.doMode(config);
                redisRepository.set(key, config, 2L, TimeUnit.DAYS);
            }
        }
        return config;
    }

    @Override
    public void cleanCacheByModel(CurrencyDataModel currencyDataModel) {
        super.cleanCacheByModel(currencyDataModel);
        String key = KeyConstant.KEY_CURRENCY_DATA_ID + currencyDataModel.getId();
        redisRepository.del(key);
    }

    @Override
    @Transactional
    public void init(Long currencyId) {
        CurrencyDataModel data = new CurrencyDataModel();
        data.setCurrencyId(currencyId);
        data.setCh("");
        data.setEn("");
        data.setIntroduction("");
        data.setIssue("");
        data.setIssueQuantity("");
        data.setCirculate("");
        data.setPrice("");
        data.setWhite("");
        data.setWebsite("");
        this.insert(data);
    }
}
