package com.liuqi.jobtask;

import com.liuqi.business.model.MiningConfigModelDto;
import com.liuqi.business.service.MiningConfigService;
import com.liuqi.business.service.MiningHandleService;
import com.liuqi.business.service.WalletStaticService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 挖矿 每日奖励
 */
@Slf4j
public class WalletStaticJob implements Job {

    @Autowired
    private WalletStaticService walletStaticService;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) {

        log.info("钱包统计开始");
        for(int i=0;i<3;i++){
            walletStaticService.walletStat(i);
        }
        log.info("钱包统计开始");
    }
}
