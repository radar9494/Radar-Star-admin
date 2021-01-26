package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.CtcPriceDto;
import com.liuqi.business.model.CtcConfigModel;
import com.liuqi.business.model.CtcConfigModelDto;

import java.util.Date;
import java.util.List;

public interface CtcConfigService extends BaseService<CtcConfigModel,CtcConfigModelDto>{

    /**
     * 是否能发布
     * （开关和时间）
     *
     * @param currencyId
     * @param tradeType
     * @param date
     * @return
     */
    boolean canPublish(Long currencyId, Integer tradeType, Date date);

    /**
     * 是否能发布
     * （开关和时间）
     *
     * @param config
     * @param tradeType
     * @param date
     * @return
     */
    boolean canPublish(CtcConfigModel config, Integer tradeType, Date date);
    /**
     * 根据币种名称获取
     * @param currencyId
     * @return
     */
    CtcConfigModelDto getByCurrencyId(Long currencyId);

    /**
     * 获取买卖价格  缓存3分钟
     * @param config
     * @return
     */
    CtcPriceDto getPrice(CtcConfigModelDto config);

    List<CtcConfigModelDto> getAll();
}
