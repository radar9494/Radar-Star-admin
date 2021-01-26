package com.liuqi.mq;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.mq.dto.SmsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.Destination;

/**
 * 生产者 发送一个消息到消息队列
 */
@Service("smsProducer")
public class SmsProducer {

    @Autowired
    private JmsTemplate jmsTemplate;
    @Resource(name = "queueSms")
    private Destination queueSms;

    /**
     * 发布消息
     * @param dto   是否取消
     */
    public void sendMessage(SmsDto dto){
        jmsTemplate.convertAndSend(queueSms,JSONObject.toJSONString(dto));
    }
}
