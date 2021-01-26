package com.liuqi.business.model;

import com.liuqi.base.FrontValid;
import lombok.Data;
import java.util.Date;
import com.liuqi.base.BaseModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;


@Data
public class UserModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *昵称
	 */
	//@NotNull(message = "昵称不能为空",groups = FrontValid.class)
	//@Length(max = 30,message = "最大30字符",groups = FrontValid.class)
	private String name;
	
	/**
	 *密码
	 */
	@NotNull(message = "密码不能为空",groups = FrontValid.class)
	private String pwd;
	
	/**
	 *交易密码
	 */
	@NotNull(message = "交易密码不能为空",groups = FrontValid.class)
	private String tradePwd;
	
	/**
	 *手机号
	 */
	private String phone;
	
	/**
	 *邮箱
	 */
	private String email;

	/**
	 *状态 0未启用  1正常  2冻结 
	 */
	private Integer status;
	

	/**
	 *密码强度 0弱  1中  2强
	 */
	private Integer pwdStrength;
	
	/**
	 *上次登录时间
	 */
	private Date lastLoginTime;

	/**
	 * 区号
	 */
	private String zone;
	/**
	 * 是否白名单 0否1是
	 */
	private Integer whiteIf;

	/**
	 *手机认证 0未认证  1已认证
	 */
	private Integer phoneAuth;
	/**
	 * 邮箱认证 0未认证 1已认证
	 */
	private Integer emailAuth;
	/**
	 * 验证类型0手机 1邮件
	 */
	private Integer authType;

	/**
	 * 机器人0不是 1是
	 */
	private Integer robot;

	/**
	 * 邀请码
	 */
	private String inviteCode;
	/**
	 * 机器人0不是 1是
	 */
	private Integer otc;

	private String  otcName;

	private Date activeDate;

	private String mnemonic;

	private String address;

	private Integer isRemember;

	private Integer googleAuth;

	private String googleSecret;

	private Integer payType;

	//交易免密类型
	private Integer tradeFree;

	private Long rank;
}
