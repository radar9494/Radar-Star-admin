package com.liuqi.third.sms.cdjs;

import cn.hutool.http.HttpRequest;
import com.liuqi.business.service.SmsConfigService;
import com.liuqi.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tanyan
 * @create 2019-12=14
 * @description
 */
@Component
public class GJSmsSendUtil {

    public static final String smsSingleRequestServerUrl = "https://mb345.com/ws/IntlSend.aspx";

    @Autowired
    private SmsConfigService smsConfigService;


    public String sendMessage(String phone, String msg) {
        String sign = smsConfigService.getSign();
        //添加签名
        msg = msg+"【"+sign +"】";
        Map<String,Object> params=new HashMap<>();
        params.put("CorpID","CorpID");
        params.put("Pwd", MD5Util.MD5Encode("Pwd"));
        params.put("Mobile","Mobile");
        params.put("Content", URLEncoder.encode(msg));
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
        params.put("Pwd", MD5Util.MD5Encode("Pwd"));
        params.put("Mobile","85000000000");
        params.put("Content", URLEncoder.encode("尊敬的用户，您的验证码123456，该验证码10分钟内有效。【中】"));
        params.put("Cell","");
        params.put("SendTime","");
        HttpRequest request=HttpRequest.get(smsSingleRequestServerUrl);
        String str=request.form(params).execute().body();
        System.out.println(request.getUrl());
        System.out.println(str);
    }
}
