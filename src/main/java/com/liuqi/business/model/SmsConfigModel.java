package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

@Data
public class SmsConfigModel extends BaseModel {

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *签名
	 */
	
	private String sign;
	
	/**
	 *国内key
	 */
	
	private String key;
	
	/**
	 *国内私钥
	 */
	
	private String secret;
	
	/**
	 *国际key
	 */
	
	private String gjkey;
	
	/**
	 *开关0关 1开
	 */
	
	private Integer onoff;
	
	/**
	 *每分钟条数
	 */
	
	private Integer minute;
	
	/**
	 *每小时条数
	 */
	
	private Integer hour;
	
	/**
	 *每天时条数
	 */
	
	private Integer day;
	
	//阿里邮件区域
	private String emailRegionId;
	//阿里邮件AccessKeyId
	private String emailAccessKeyId;
	//阿里邮件Secret
	private String emailSecret;
	//阿里邮件账号
	private String emailAccountName;
	//阿里邮件标签
	private String emailTag;

}
