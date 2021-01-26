package com.liuqi.spring;

import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.model.CurrencyAreaModelDto;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.service.CurrencyAreaService;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.business.service.TradeService;
import com.liuqi.business.trade.TradeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 项目启动后 跑撮合
 */
@Component
public class TradeRunnerImpl implements ApplicationRunner{
    @Autowired
    private CurrencyAreaService currencyAreaService;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private TradeRequest tradeRequest;


    /**
     * 启动撮合
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        /** 查询所有启用币种 缓存（不分页） */
        List<CurrencyAreaModelDto> areaList = currencyAreaService.findAllCanUseArea();
        if (areaList != null && areaList.size() > 0) {
            for (CurrencyAreaModelDto area : areaList) {
                /** 获取交易区的可用交易对 */
                List<CurrencyTradeModelDto> tradeList = currencyTradeService.getCanUseTradeInfoByArea(area.getId());
                if (tradeList != null && tradeList.size() > 0) {
                    for (CurrencyTradeModelDto trade : tradeList) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                tradeRequest.request(trade.getId());
                            }
                        }).start();
                    }
                }
            }
        }
    }
}
