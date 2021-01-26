package com.liuqi.business.service.impl;


import com.liuqi.business.model.SmsConfigModel;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.model.UserModelDto;
import com.liuqi.business.service.AuthCodeService;
import com.liuqi.business.service.SmsConfigService;
import com.liuqi.business.service.UserService;
import com.liuqi.mq.EmailProducer;
import com.liuqi.mq.SmsProducer;
import com.liuqi.mq.dto.EmailDto;
import com.liuqi.mq.dto.SmsDto;
import com.liuqi.third.email.AliEmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthCodeServiceImpl implements AuthCodeService {

	@Autowired
	private EmailProducer emailProducer;
	@Autowired
	private SmsProducer smsProducer;
	@Autowired
	private SmsConfigService smsConfigService;
	@Autowired
	private UserService userService;
	@Autowired
	private AliEmailSender aliEmailSender;
	/**
	 * 发送验证码
	 *
	 * @param account
	 */
	@Override
	public void sendVerifyCode(String account, boolean isChain, String randomCode, Integer authType) {
		SmsConfigModel config=smsConfigService.getConfig();
		String sign=config.getSign();
		String msg_temp="";
		if (!isChain) {
			msg_temp = "Respected "+sign+" Users, your authentication code "+randomCode+" is valid within 10 minutes. Be careful of being fished! ";
		} else {
			msg_temp = "尊敬的"+sign+"用户，您的验证码" + randomCode + "，该验证码10分钟内有效，操作前请确定地址栏的域名，谨防被钓鱼！";
		}
		System.out.println(msg_temp);
		if (authType.equals(UserModelDto.AUTHTYPE_EMAIL)) {//发送邮件验证
		     //改为直接发
			aliEmailSender.sendSimpleMail(account, sign+"验证码",msg_temp,sign);
			//emailProducer.sendMessage(new EmailDto(msg_temp, account,sign+"验证码",sign));
		}else{//手机发送验证码
			smsProducer.sendMessage(new SmsDto(isChain,account,msg_temp));
		}

	}

	/**
	 * 发送充值提现
	 * @param userId
	 * @param msg
	 * @param title
	 */
	@Override
	public void sendRechargeExtractSms(Long userId,String msg,String title) {
		UserModel user=userService.getById(userId);
		SmsConfigModel config=smsConfigService.getConfig();
		Integer sendType = UserModelDto.AUTHTYPE_PHONE;
		boolean isChain=true;
		String account=user.getPhone();
		//发送类型 1短信 2邮件
		if (user.getAuthType().equals(UserModelDto.AUTHTYPE_EMAIL)) {
			account = user.getEmail();
			isChain = true;
			sendType = UserModelDto.AUTHTYPE_EMAIL;

			emailProducer.sendMessage(new EmailDto(msg, account,config.getSign()+title,config.getSign()));
		} else {//短信
			account = user.getPhone();
			//非中国手机  添加区号发送
			if (!"86".equals(user.getZone())) {
				isChain = false;
				//发送时添加区号
				account = user.getZone() + account;
			}

			smsProducer.sendMessage(new SmsDto(isChain,account,msg));
		}
	}



}
