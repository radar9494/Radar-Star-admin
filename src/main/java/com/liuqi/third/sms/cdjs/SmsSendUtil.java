package com.liuqi.third.sms.cdjs;

import cn.hutool.http.HttpRequest;
import com.liuqi.business.service.SmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tanyan
 * @create 2019-12=14
 * @description
 */
@Component
public class SmsSendUtil {

    public static final String smsSingleRequestServerUrl = "https://sdk2.028lk.com/sdk2/BatchSend2.aspx";

    @Autowired
    private SmsConfigService smsConfigService;


    public String sendMessage(String phone, String msg) {
        String sign = smsConfigService.getSign();
        //添加签名
        msg = msg+"【"+sign +"】";
        Map<String,Object> params=new HashMap<>();
        params.put("CorpID","CorpID");
        params.put("Pwd","Pwd");
        params.put("Mobile",phone);
        params.put("Content",msg);
        params.put("Cell","");
        params.put("SendTime","");
        HttpRequest request=HttpRequest.post(smsSingleRequestServerUrl);
        String str=request.form(params).execute().body();
        System.out.println(str);
        return str;
    }



    public static void main(String[] args) {
        Map<String,Object> params=new HashMap<>();
        params.put("CorpID","CorpID");
        params.put("Pwd","Pwd");
        params.put("Mobile","18600000000");
        params.put("Content","尊敬的***用户，您的验证码123456，该验证码10分钟内有效。【***】");
        params.put("Cell","");
        params.put("SendTime","");
        HttpRequest request=HttpRequest.post("https://sdk2.028lk.com/sdk2/BatchSend2.aspx");
        String str=request.form(params).execute().body();
        System.out.println(str);
    }
}
