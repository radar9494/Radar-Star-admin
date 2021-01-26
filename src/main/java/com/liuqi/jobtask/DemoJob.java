package com.liuqi.jobtask;

import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.DateTimeUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * 例子
 */
public class DemoJob implements Job {
    private static Log log = Log4j2LogFactory.get("autoTask");
    @Autowired
    private RedisRepository redisRepository;
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("demo定时任务开始");
        String key = DateTimeUtils.currentDate("yyyy-MM-dd") + ":demo";
        if (!redisRepository.hasKey(key)) {
            log.info("demo定时任务执行");
           this.doIt();
            //一天时间
            redisRepository.set(key, "nwc", 1L, TimeUnit.DAYS);
        }else{
            log.info("demo定时任务执行过，不在执行");
        }
        log.info("demo定时任务结束");
    }


    //业务逻辑
    private void doIt(){
        System.out.println("------》"+DateTimeUtils.currentDateTime());
    }
}
