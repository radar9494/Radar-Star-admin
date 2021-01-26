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
public class SmsYHCXUSender {

    public static final String charset = "utf-8";

    public static final String smsSingleRequestServerUrl = "http://120.55.126.176:80/api/v2/sendSms.json";

    @Autowired
    private SmsConfigService smsConfigService;


    public String sendMessage(String phone, String msg) {
        String sign = smsConfigService.getSign();
        String appKey = smsConfigService.getKey();
        String appSecret = smsConfigService.getSecret();
        //添加签名
        msg = "【"+sign +"】"+ msg;
        HttpRequest request = HttpRequest.post(smsSingleRequestServerUrl);
        request.form("appKey", appKey);
        request.form("appSecret", appSecret);
        request.form("phones", phone);
        request.form("content", msg);
        String str = request.timeout(5000).execute().body();
        System.out.println("短信发送返回--》" + str);
        JSONObject obj = JSONObject.parseObject(str);
        return obj.getString("errorCode");
    }

    public static void main(String[] args) {

        String appKey = "kKLGjF0ypb08jeOHNWeo6bP3ZQdEhZ3z";
        String appSecret = "3a0ca5d7efcd184c235af9afb980e5fd";
        String phone = "18674006013";
        String msg = "尊敬的BOKC用户，您的验证码123456 ，该验证码10分钟内有效，操作前请确定地址栏的域名，谨防被钓鱼！";
        String sign = "【TDA】";

        msg = sign + msg;
        HttpRequest request = HttpRequest.post(smsSingleRequestServerUrl);
        request.form("appKey", appKey);
        request.form("appSecret", appSecret);
        request.form("phones", phone);
        request.form("content", msg);
        String str = request.timeout(5000).execute().body();
        System.out.println("短信发送返回--》" + str);
        JSONObject obj = JSONObject.parseObject(str);
    }

}
