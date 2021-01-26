package com.liuqi.jobtask;

import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.business.enums.WalletDoEnum;
import com.liuqi.business.model.TradeRecordModelDto;
import com.liuqi.business.service.TradeRecordService;
import com.liuqi.mq.TradeWalletProducer;
import com.liuqi.mq.dto.TradeWalletDto;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 交易资产漏发放任务
 * 3/5 * * * * ?
 */
public class TradeWalletJob implements Job {
    private static Log log = Log4j2LogFactory.get("autoTask");
    @Autowired
    private TradeRecordService tradeRecordService;
    @Autowired
    private TradeWalletProducer tradeWalletProducer;

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //查询未处理的卖单
        TradeRecordModelDto search = new TradeRecordModelDto();
        search.setBuyWalletStatus(WalletDoEnum.NOT.getCode());
        List<TradeRecordModelDto> list = tradeRecordService.queryListByDto(search, false);
        if (list != null && list.size() > 0) {
            for (TradeRecordModelDto dto : list) {
                tradeWalletProducer.sendMessage(new TradeWalletDto(dto.getId(), true, false));
            }
        }

        search = new TradeRecordModelDto();
        search.setSellWalletStatus(WalletDoEnum.NOT.getCode());
        list = tradeRecordService.queryListByDto(search, false);
        if (list != null && list.size() > 0) {
            for (TradeRecordModelDto dto : list) {
                tradeWalletProducer.sendMessage(new TradeWalletDto(dto.getId(), false, true));
            }
        }
    }
}
