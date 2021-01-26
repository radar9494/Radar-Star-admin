package com.liuqi.jobtask;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.business.service.ServiceChargeService;
import com.liuqi.business.service.SuperNodeChargeService;
import com.liuqi.business.service.SuperNodeSendService;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.DateTimeUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 统计手续费
 *      3 0 0 * * ?
 */
public class TotalChargeJob implements Job {
    private static Log log = Log4j2LogFactory.get("autoTask");
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private ServiceChargeService serviceChargeService;
    @Autowired
    private SuperNodeChargeService superNodeChargeService;
    @Autowired
    private SuperNodeSendService superNodeSendService;

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("手续费汇总定时任务开始");
        String key= DateTimeUtils.currentDate("yyyy-MM-dd")+":totalCharge";
        if (!redisRepository.hasKey(key)) {
            log.info("手续费汇总定时任务执行");
            this.doIt();
            //一天时间
            redisRepository.set(key, DateTimeUtils.currentDateTime(), 23L, TimeUnit.HOURS);
        }else{
            log.info("手续费汇总定时任务执行过，不在执行");
        }
        log.info("手续费汇总定时任务结束");
    }


    //业务逻辑
    private void doIt(){
        //昨天时间
        Date date = DateTime.now().offset(DateField.DAY_OF_YEAR, -1);
        try {
            serviceChargeService.totalCharge(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(5000L);
            log.info("超级节点手续费汇总开始");
            superNodeChargeService.totalCharge(date);
            log.info("超级节点手续费汇总结束");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(5000L);
            log.info("超级节点手续费分红开始");
            superNodeSendService.createChargeOrder(date);
            log.info("超级节点手续费分红结束");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(5000L);
            log.info("超级节点手续费分红开始");
            superNodeSendService.realse();
            log.info("超级节点手续费分红结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
