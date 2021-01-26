package com.liuqi.jobtask;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.enums.WalletDoEnum;
import com.liuqi.business.model.ChargeAwardModelDto;
import com.liuqi.business.service.ChargeAwardService;
import com.liuqi.redis.lock.RedissonLockUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 手续费释放发放
 * 13 0/1 * * * ?
 */
public class ChargeWalletJob implements Job {
    private static Log log = Log4j2LogFactory.get("autoTask");
    @Autowired
    private ChargeAwardService chargeAwardService;

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //查询未处理的卖单
        ChargeAwardModelDto search = new ChargeAwardModelDto();
        search.setStatus(WalletDoEnum.NOT.getCode());
        search.setEndCreateTime(DateTime.now().offset(DateField.SECOND, -10));
        search.setSortName("create_time");
        search.setSortType("asc");
        List<ChargeAwardModelDto> list = chargeAwardService.queryListByDto(search, false);
        if (list != null && list.size() > 0) {
            for (ChargeAwardModelDto dto : list) {
                this.doIt(dto.getId());
            }
        }

    }

    //业务逻辑
    private void doIt(Long releaseId) {
        String key = LockConstant.LOCK_CHARGE_AWARD_ID + releaseId;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            chargeAwardService.recordRelease(releaseId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }
}
