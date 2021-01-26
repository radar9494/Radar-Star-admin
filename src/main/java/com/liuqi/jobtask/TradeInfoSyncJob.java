package com.liuqi.jobtask;

import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.business.async.AsyncTask;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.CurrencyAreaModelDto;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.service.AutoExtractService;
import com.liuqi.business.service.CurrencyAreaService;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.business.service.TradeInfoCacheService;
import com.liuqi.business.service.impl.AutoExtractServiceImpl;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.util.List;

/**
 * 交易信息（1/5 * * * * ?）
 *      买卖单查询数据库 同步到redis缓存
 */
public class TradeInfoSyncJob implements Job {
    private static Log log = Log4j2LogFactory.get("autoTask");
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private CurrencyAreaService currencyAreaService;
    @Autowired
    @Lazy
    private AsyncTask asyncTask;

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        this.doIt();
    }

    //业务逻辑
    private void doIt() {
        List<CurrencyAreaModelDto> areaList = currencyAreaService.findAllCanUseArea();
        if (areaList != null && areaList.size() > 0) {
            for (CurrencyAreaModelDto area : areaList) {
                List<CurrencyTradeModelDto> tradeList = currencyTradeService.getCanUseTradeInfoByArea(area.getId());
                if (tradeList != null && tradeList.size() > 0) {
                    for (CurrencyTradeModelDto trade : tradeList) {
                        //交易开关  开的推送
                        if(SwitchEnum.ON.getCode().equals(trade.getTradeSwitch())) {
                            asyncTask.syncInfoTask(trade.getId());
                        }
                    }
                }
            }
        }
    }
}
