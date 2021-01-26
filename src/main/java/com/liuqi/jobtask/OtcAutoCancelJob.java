package com.liuqi.jobtask;

import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.model.OtcOrderRecordModel;
import com.liuqi.business.service.OtcOrderRecordService;
import com.liuqi.redis.lock.RedissonLockUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * otc自动取消
 */
public class OtcAutoCancelJob implements Job {
    private static Log log = Log4j2LogFactory.get("autoTask");
    @Autowired
    private OtcOrderRecordService otcOrderRecordService;

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Long recordId = jobExecutionContext.getJobDetail().getJobDataMap().getLong("id");
        int status = jobExecutionContext.getJobDetail().getJobDataMap().getInt("status");
        log.info("otc自动取消："+recordId);
        this.doIt(recordId,status);
        log.info("otc自动取消结束");
    }


    //业务逻辑
    private void doIt(Long recordId,Integer status){
        OtcOrderRecordModel record = otcOrderRecordService.getById(recordId);
        if (record == null) {
            return ;
        }
        String key = LockConstant.LOCK_OTC_ORDER + record.getOrderId();
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            otcOrderRecordService.autoCancel(recordId,status);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }
}
