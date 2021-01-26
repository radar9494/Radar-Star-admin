package com.liuqi.jobtask;

import com.liuqi.business.async.AsyncTask;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.CurrencyAreaModelDto;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.service.CurrencyAreaService;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.business.websocket.CollectPushHandle;
import com.liuqi.mq.TradeTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutoPushJob {
    @Autowired
    private TradeTopic tradeTopic;
    @Autowired
    private CurrencyAreaService currencyAreaService;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    @Lazy
    private AsyncTask asyncTask;
    @Autowired
    private CollectPushHandle collectPushHandle;

    @Scheduled(cron = " 1/2 * * * * ?")
    public void tradePush(){
        boolean push=true;
        List<CurrencyAreaModelDto> areaList = currencyAreaService.findAllCanUseArea();
        if (areaList != null && areaList.size() > 0) {
            for (CurrencyAreaModelDto area : areaList) {
                List<CurrencyTradeModelDto> tradeList = currencyTradeService.getCanUseTradeInfoByArea(area.getId());
                if (tradeList != null && tradeList.size() > 0) {
                    for (CurrencyTradeModelDto trade : tradeList) {
                        //交易开关开启 并且推送  开的推送买卖，成交 深度图
                        if (SwitchEnum.ON.getCode().equals(trade.getTradeSwitch())) {
                            asyncTask.pushInfoTask(trade.getId());
                        }
                    }
                }
            }
        }
    }


    @Scheduled(cron = " 1/5 * * * * ?")
    public void tradeInfoPush(){
        List<CurrencyAreaModelDto> areaList = currencyAreaService.findAllCanUseArea();
        if (areaList != null && areaList.size() > 0) {
            for (CurrencyAreaModelDto area : areaList) {
                tradeTopic.sendAreaMessage(area.getId());
            }
        }
    }


    @Scheduled(cron = " 1/5 * * * * ?")
    public void collectPush() {
        collectPushHandle.pushAll();
    }

}
