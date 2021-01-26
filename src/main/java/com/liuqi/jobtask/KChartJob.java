package com.liuqi.jobtask;

import com.liuqi.business.enums.KTypeEnum;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.CurrencyAreaModelDto;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.service.CurrencyAreaService;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.business.service.KDataService;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * K线图 1分钟  5分钟 15分钟 30分钟 60分钟 1天
 */
public class KChartJob implements Job {

    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private KDataService kDataService;
    @Autowired
    private CurrencyAreaService currencyAreaService;

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //获取当前时间减一分钟  秒数为0
        DateTime endTime = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0);
        List<CurrencyAreaModelDto> areaList = currencyAreaService.findAllCanUseArea();
        if (areaList != null && areaList.size() > 0) {
            for (CurrencyAreaModelDto area : areaList) {
                //查询交易对
                List<CurrencyTradeModelDto> tradeList = currencyTradeService.getCanUseTradeInfoByArea(area.getId());
                if (tradeList != null && tradeList.size() > 0) {
                    for (CurrencyTradeModelDto trade : tradeList) {
                        //交易开关开启 并且推送  开的推送买卖，成交 深度图
                        if (SwitchEnum.ON.getCode().equals(trade.getTradeSwitch())) {
                            execute(trade,endTime);
                        }
                    }
                }
            }
        }
    }


    public void execute(CurrencyTradeModelDto trade,DateTime endTime){
        this.oneM(trade,endTime);
        int minute=endTime.getMinuteOfHour();
        //五分钟
        if(minute%5==0){
            this.fiveM(trade,endTime);
        }
        //十五分钟
        if(minute%15==0){
            this.fifteenM(trade,endTime);
        }
        //30分钟
        if(minute%30==0){
            this.thirtyM(trade,endTime);
        }
        //一小时
        if(minute==0){
            this.oneH(trade,endTime);
        }
        //1天
        int hour=endTime.getHourOfDay();
        if(hour==0&&minute==0){
            this.oneD(trade,endTime);
        }
        //1天  周1
        int week=endTime.getDayOfWeek();
        if(week==1 && hour==0 && minute==0){
            this.oneW(trade,endTime);
        }
    }


    //一分钟任务
    private void oneM(CurrencyTradeModelDto trade,DateTime endTime){
        try {
            DateTime startTime = endTime.plusMinutes(-1);
            kDataService.storeKChartData(trade,KTypeEnum.ONEM.getCode(),startTime.toDate(),endTime.toDate());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //五分钟任务
    private void fiveM(CurrencyTradeModelDto trade,DateTime endTime){
        try {
            DateTime startTime = endTime.plusMinutes(-5);
            kDataService.storeKChartData(trade,KTypeEnum.FIVEM.getCode(),startTime.toDate(),endTime.toDate());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //十五分钟任务
    private void fifteenM(CurrencyTradeModelDto trade,DateTime endTime){
        try {
            DateTime startTime = endTime.plusMinutes(-15);
            kDataService.storeKChartData(trade,KTypeEnum.FIFTEENM.getCode(),startTime.toDate(),endTime.toDate());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //三十分钟任务
    private void thirtyM(CurrencyTradeModelDto trade,DateTime endTime){
        try {
            DateTime startTime = endTime.plusMinutes(-30);
            kDataService.storeKChartData(trade,KTypeEnum.THIRTYM.getCode(),startTime.toDate(),endTime.toDate());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //1小时
    private void oneH(CurrencyTradeModelDto trade,DateTime endTime){
        try {
            DateTime startTime = endTime.plusHours(-1);
            kDataService.storeKChartData(trade,KTypeEnum.ONEH.getCode(),startTime.toDate(),endTime.toDate());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //一天
    private void oneD(CurrencyTradeModelDto trade,DateTime endTime){
        try {
            DateTime startTime = endTime.plusDays(-1);
            kDataService.storeKChartData(trade,KTypeEnum.ONED.getCode(),startTime.toDate(),endTime.toDate());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //一周
    private void oneW(CurrencyTradeModelDto trade,DateTime endTime){
        try {
            DateTime startTime = endTime.plusWeeks(-1);
            kDataService.storeKChartData(trade,KTypeEnum.ONEW.getCode(),startTime.toDate(),endTime.toDate());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
