package com.liuqi.mq;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.mq.dto.ChargeDto;
import com.liuqi.mq.dto.ReleaseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.Destination;

/**
 * 手续费奖励
 */
@Service("chargeProducer")
public class ChargeProducer {

    @Autowired
    private JmsTemplate jmsTemplate;
    @Resource(name = "queueCharge")
    private Destination queueCharge;
    /**
     * 发布消息
     *
     */
    public void chargeMessage(ChargeDto dto) {
        jmsTemplate.convertAndSend(queueCharge, JSONObject.toJSONString(dto));
    }

}
