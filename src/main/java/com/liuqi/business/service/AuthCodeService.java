package com.liuqi.business.service;

public interface AuthCodeService {

	/**
	 * 发送验证码
	 * @param account  节后号码
	 * @param isChain  中英文
	 * @param randomCode 验证码
	 * @param sendType  发送类型
	 */
	void sendVerifyCode(String account, boolean isChain, String randomCode,Integer sendType);

	/**
	 * 充提短信
	 * @param userId
	 * @param msg
	 * @param title
	 */
	void sendRechargeExtractSms(Long userId,String msg,String title);
}
