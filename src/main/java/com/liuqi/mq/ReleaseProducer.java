package com.liuqi.mq;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.mq.dto.ReleaseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.Destination;

/**
 * 释放
 */
@Service("releaseProducer")
public class ReleaseProducer {

    @Autowired
    private JmsTemplate jmsTemplate;
    @Resource(name = "queueBuyRelease")
    private Destination queueBuyRelease;
    @Resource(name = "queueSellRelease")
    private Destination queueSellRelease;
    /**
     * 发布消息
     *
     */
    public void sendBuyMessage(ReleaseDto dto) {
        jmsTemplate.convertAndSend(queueBuyRelease, JSONObject.toJSONString(dto));
    }

    /**
     * 发布消息
     */
    public void sendSellMessage(ReleaseDto dto) {
        jmsTemplate.convertAndSend(queueSellRelease, JSONObject.toJSONString(dto));
    }
}
