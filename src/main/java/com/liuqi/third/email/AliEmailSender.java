package com.liuqi.third.email;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.liuqi.business.model.EmailModel;
import com.liuqi.business.model.SmsConfigModelDto;
import com.liuqi.business.service.EmailService;
import com.liuqi.business.service.SmsConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 阿里邮件
 */
@Component
@Slf4j
public class AliEmailSender {

    public static final String ACCESSKEY="ACCESSKEY";
    public static final String ACCESSSECRET="ACCESSSECRET";

    @Autowired
    private EmailService emailService;

    public void sendSimpleMail(String to,String subject,String content,String sign) {
        EmailModel email=emailService.getCanUsing();
        // 如果是除杭州region外的其它region（如新加坡、澳洲Region），需要将下面的"cn-hangzhou"替换为"ap-southeast-1"、或"ap-southeast-2"。
        IClientProfile profile = DefaultProfile.getProfile(email.getRegionId(), email.getAccessKeyId(), email.getSecret());
        // 如果是除杭州region外的其它region（如新加坡region）， 需要做如下处理
        /*try {
            DefaultProfile.addEndpoint("dm.ap-southeast-1.aliyuncs.com", "ap-southeast-1", "Dm",  "dm.ap-southeast-1.aliyuncs.com");
        } catch (ClientException e) {
        e.printStackTrace();
        }*/
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {
            //request.setVersion("2017-06-22");// 如果是除杭州region外的其它region（如新加坡region）,必须指定为2017-06-22
            request.setAccountName(email.getAccountName());//控制台创建的发信地址
            request.setFromAlias(sign);//发信人昵称
            request.setAddressType(1);
            request.setTagName(email.getTag());//控制台创建的标签
            request.setReplyToAddress(true);
            request.setToAddress(to);
            request.setSubject(subject);
            request.setHtmlBody(content);
            SingleSendMailResponse httpResponse = client.getAcsResponse(request);
            log.info("邮件发送成功--》"+content);
        } catch (Exception e) {
            e.printStackTrace();
            log.info(email.getAccessKeyId()+"邮件发送异常--》"+to+"-->"+content);
        }
    }
}
