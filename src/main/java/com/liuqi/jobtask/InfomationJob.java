package com.liuqi.jobtask;

import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.business.service.InformationService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 资讯获取
 * 3 2/1 * * * ?
 */
public class InfomationJob implements Job {
    private static Log log = Log4j2LogFactory.get("autoTask");

    @Autowired
    private InformationService informationService;


    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        this.doIt();
    }

    //业务逻辑
    private void doIt(){
        informationService.thridInfo();
    }
}
