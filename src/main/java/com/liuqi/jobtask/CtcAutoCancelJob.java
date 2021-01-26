package com.liuqi.jobtask;

import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.service.CtcOrderService;
import com.liuqi.redis.lock.RedissonLockUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ctc自动取消
 */
public class CtcAutoCancelJob implements Job {
    private static Log log = Log4j2LogFactory.get("autoTask");
    @Autowired
    private CtcOrderService ctcOrderService;

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Long recordId = jobExecutionContext.getJobDetail().getJobDataMap().getLong("id");
        log.info("ctc自动取消："+recordId);
        this.doIt(recordId);
        log.info("ctc自动取消结束");
    }


    //业务逻辑
    private void doIt(Long recordId){
        String key = LockConstant.LOCK_CTC_RECORD + recordId;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            ctcOrderService.autoCancel(recordId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }
}
