package com.liuqi.mq;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.mq.dto.KDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.Destination;

/**
 * 生产者 发送一个消息到消息队列
 */
@Service("curKDataProducer")
public class CurKDataProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Resource(name = "queueCurKData")
    private Destination queueCurKData;

    /**
     * 生成当前K线
     *
     * @param dto
     */
    public void curKData(KDataDto dto) {
        jmsTemplate.convertAndSend(queueCurKData, JSONObject.toJSONString(dto));
    }

}
