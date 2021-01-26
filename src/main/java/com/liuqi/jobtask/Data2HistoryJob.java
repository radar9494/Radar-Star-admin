package com.liuqi.jobtask;

import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.business.service.TrusteeService;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.DateTimeUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * 历史数据迁移
 */
public class Data2HistoryJob implements Job {
    private static Log log = Log4j2LogFactory.get("autoTask");
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private TrusteeService trusteeService;


    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("历史数据迁移任务开始");
        String key = "job:charge:" + DateTimeUtils.currentDate("yyyy-MM-dd");
        if (!redisRepository.hasKey(key)) {
            log.info("历史数据迁移任务执行");
            redisRepository.set(key, DateTimeUtils.currentDateTime(), 30L, TimeUnit.MINUTES);
            try {
                log.info("历史数据迁移任务执行2-删除机器人交易订单数据");
                trusteeService.deleteRobotCancel();
                log.info("历史数据迁移任务执行2-删除机器人交易订单数据-success");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("历史数据迁移任务执行2-删除机器人交易订单数据fail-" + e.getMessage());
            }
        } else {
            log.info("历史数据迁移已执行，不在执行");
        }
        log.info("历史数据迁移任务结束");
    }
}
