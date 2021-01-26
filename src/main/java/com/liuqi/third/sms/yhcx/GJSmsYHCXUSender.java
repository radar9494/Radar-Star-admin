package com.liuqi.third.sms.yhcx;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.service.SmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author tianyh
 * @Description:普通短信发送
 */
@Component
public class GJSmsYHCXUSender {

    public static final String charset = "utf-8";

    public static final String smsSingleRequestServerUrl = "https://sms.yunpian.com/v2/sms/single_send.json";

    @Autowired
    private SmsConfigService smsConfigService;

    public String sendMessage(String phone, String msg) {
        String sign = smsConfigService.getSign();
        String appKey = smsConfigService.getGjKey();
        //添加签名
        msg = "【"+sign +"】"+ msg;
        HttpRequest request = HttpRequest.post(smsSingleRequestServerUrl);
        request.form("apikey", appKey);
        request.form("mobile", "+"+phone);
        request.form("text", msg);
        String str = request.timeout(5000).execute().body();
        System.out.println("短信发送返回--》" + str);
        JSONObject obj = JSONObject.parseObject(str);
        return obj.getString("errorCode");
    }

    public static void main(String[] args) {

        String API="mojingyuan";

        String appKey = "2b52ad7e2212ac361cf655b79e97ccaf";
        String phone = "+8618674006013";
        String sign = "ET";
        String msg = "Respected "+sign+" Users, your authentication code "+123456+" is valid within 10 minutes. Be careful of being fished!";
        msg = "【"+sign+"】" + msg;
        System.out.println(msg);
        HttpRequest request = HttpRequest.post(smsSingleRequestServerUrl);
        request.form("apikey", appKey);
        request.form("mobile", phone);
        request.form("text", msg);
        String str = request.timeout(5000).execute().body();
        System.out.println("短信发送返回--》" + str);
        JSONObject obj = JSONObject.parseObject(str);
    }

}
