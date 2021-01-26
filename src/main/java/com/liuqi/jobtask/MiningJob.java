package com.liuqi.jobtask;

import com.liuqi.business.service.MiningHandleService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 挖矿转入转出
 */
public class MiningJob implements Job {

    @Autowired
    private MiningHandleService miningHandleService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        miningHandleService.taskHandle(jobDataMap.getLong("id"));
    }
}
