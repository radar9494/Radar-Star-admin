package com.liuqi.business.service.impl;


import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.dto.TradeInfoDto;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.service.ConfigService;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.business.service.IndexService;
import com.liuqi.business.service.TradeService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class IndexServiceImpl implements IndexService {
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private ConfigService configService;

    /**
     * 查询常用交易对
     *
     * @return
     */
    @Override
    public List<TradeInfoDto> indexCommTrade() {
        String config = configService.queryValueByName(ConfigConstant.CONFIGNAME_TRADE_SHOW);
        List<TradeInfoDto> commTradeList = new ArrayList<TradeInfoDto>();
        if (config != null) {
            String[] tradeIdArr = config.split(",");
            for (String tradeIds : tradeIdArr) {
                CurrencyTradeModelDto trade = currencyTradeService.getById(Long.valueOf(tradeIds));
                if (trade != null) {
                    //获取币种名称  缓存中的数据
                    TradeInfoDto dto = tradeService.getByCurrencyAndTradeType(trade);
                    commTradeList.add(dto);
                }
            }
        }
        return commTradeList;
    }


    /**
     * 查询区域交易对
     *
     * @param areaId
     * @return
     */
    @Override
    public List<TradeInfoDto> getByAreaId(Long areaId) {
        //前台的交易区
        List<TradeInfoDto> dtoList = new ArrayList<TradeInfoDto>();
        //获取显示的交易信息  缓存数据
        List<CurrencyTradeModelDto> tradeList = currencyTradeService.getCanUseTradeInfoByArea(areaId);
        //每个交易对的交易信息
        if (tradeList != null && tradeList.size() > 0) {
            for (CurrencyTradeModelDto trade : tradeList) {
                //使用缓存 按交易区缓存 1秒钟查询一次
                TradeInfoDto dto = tradeService.getByCurrencyAndTradeType(trade);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }


}
