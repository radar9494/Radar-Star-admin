package com.liuqi.jobtask;

import com.liuqi.business.async.AsyncTask;
import com.liuqi.business.dto.RechargeSearchDto;
import com.liuqi.business.model.CurrencyModel;
import com.liuqi.business.service.CurrencyService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;

/**
 * 充值
 * 2/5 * * * * ?
 */
public class RechargeJob implements Job {
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    @Lazy
    private AsyncTask asyncTask;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<RechargeSearchDto> list = currencyService.getRecharge();
        if (list != null && list.size() > 0) {
            CurrencyModel currency=null;
            for (RechargeSearchDto search : list) {
                currency=currencyService.getById(search.getId());
                asyncTask.searchRecharge(currency,search.getProtocol(),search.getThirdCurrency(),search.getConfirm());
            }
        }
    }
}
