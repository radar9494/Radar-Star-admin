package com.liuqi.third.sms;

import com.liuqi.third.sms.cdjs.GJSmsSendUtil;
import com.liuqi.third.sms.cdjs.SmsSendUtil;
import com.liuqi.third.sms.yhcx.GJSmsYHCXUSender;
import com.liuqi.third.sms.yhcx.SmsYHCXUSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 短信发送
 */
@Component
@Slf4j
public class SmsSender {


    @Autowired
    private SmsYHCXUSender smsYHCXUSender;
    @Autowired
    private GJSmsYHCXUSender gjSmsYHCXUSender;
    @Autowired
    private SmsSendUtil smsSendUtil;
    @Autowired
    private GJSmsSendUtil gJSmsSendUtil;
    /**
     * 发送短信
     * @param isChain
     * @param phone
     * @param message
     */
    public void sendSms(boolean isChain, String phone, String message) {
        if (!isChain) {
            //String code = gjSmsYHCXUSender.sendMessage(phone, message);
            String code = gJSmsSendUtil.sendMessage(phone, message);
            log.info(phone + "短信验证码发送返回--》" + message + "-->" + code);
        } else {
            //String code = smsYHCXUSender.sendMessage(phone, message);
            String code =smsSendUtil.sendMessage(phone, message);
            log.info(phone + "短信验证码发送返回--》" + message + "-->" + code);
        }
    }
}
