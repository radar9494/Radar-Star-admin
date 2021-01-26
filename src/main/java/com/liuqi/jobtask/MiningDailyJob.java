package com.liuqi.jobtask;

import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.business.model.MiningConfigModelDto;
import com.liuqi.business.service.MiningConfigService;
import com.liuqi.business.service.MiningHandleService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 挖矿 每日奖励
 */

public class MiningDailyJob implements Job {

    @Autowired
    private MiningHandleService miningHandleService;
    @Autowired
    private MiningConfigService miningConfigService;
    private static Log log = Log4j2LogFactory.get("autoTask");
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        MiningConfigModelDto search=new MiningConfigModelDto();
        search.setType(0);
        List<MiningConfigModelDto> list = miningConfigService.queryListByDto(search, false);

        log.info("矿池收益开始");
       //矿池的静动态
        for(MiningConfigModelDto item:list){
            miningHandleService.income(item.getCurrencyId());
        }
        log.info("矿池收益结束");
    }
}
