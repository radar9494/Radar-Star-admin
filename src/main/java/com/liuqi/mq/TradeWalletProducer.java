package com.liuqi.mq;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.mq.dto.TradeWalletDto;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.Destination;
import java.util.concurrent.TimeUnit;

/**
 * 生产者 处理钱包
 */
@Service("tradeWalletProducer")
public class TradeWalletProducer {

    @Autowired
    private JmsTemplate jmsTemplate;
    @Resource(name = "queueTradeWalletB")
    private Destination queueTradeWalletB;
    @Resource(name = "queueTradeWalletS")
    private Destination queueTradeWalletS;
    @Autowired
    private RedisRepository redisRepository;
    /**
     * 发布消息
     *
     * @param dto 是否取消
     */
    public void sendMessage(TradeWalletDto dto) {
        if(dto.isBuy()) {
            //5秒钟一次
            String key= KeyConstant.KEY_RECORD_RUN_TIME+dto.getRecordId()+"_"+ BuySellEnum.BUY.getCode();
            if (!redisRepository.hasKey(key)) {
                redisRepository.set(key, DateTimeUtils.currentDateTime(), 5L, TimeUnit.SECONDS);
                jmsTemplate.convertAndSend(queueTradeWalletB, JSONObject.toJSONString(dto));
            }
        }
        if(dto.isSell()) {
            //5秒发送一次
            String key= KeyConstant.KEY_RECORD_RUN_TIME+dto.getRecordId()+"_"+ BuySellEnum.SELL.getCode();
            if (!redisRepository.hasKey(key)) {
                redisRepository.set(key, DateTimeUtils.currentDateTime(), 5L, TimeUnit.SECONDS);
                jmsTemplate.convertAndSend(queueTradeWalletS, JSONObject.toJSONString(dto));
            }
        }
    }
}
