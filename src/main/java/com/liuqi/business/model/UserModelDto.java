package com.liuqi.business.model;

import com.liuqi.base.FrontValid;
import com.liuqi.business.enums.UserAuthTypeEnum;
import com.liuqi.business.enums.UserPwdEnum;
import com.liuqi.business.enums.UserStatusEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


@Data
public class UserModelDto extends UserModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	//认证  0手机 1邮件
	public static final int AUTHTYPE_PHONE = 0;
	public static final int AUTHTYPE_EMAIL = 1;

	private String statusStr;
	private String authStatusStr;
	private String pwdStrengthStr;

	public String getStatusStr() {
		return UserStatusEnum.getName(super.getStatus());
	}

	public String getPwdStrengthStr() {
		return UserPwdEnum.getName(super.getPwdStrength());
	}
	/**
	 * 白名单显示
	 */
	private String whiteIfStr;

	public String getWhiteIfStr() {
		return YesNoEnum.getName(super.getWhiteIf());
	}

	public UserModelDto init(UserModelDto leader) {
		this.setCreateTime(new Date());
		this.setUpdateTime(new Date());
		this.setStatus(UserStatusEnum.USDING.getCode());
		return this;
	}
	private String phoneAuthStr;
	private String emailAuthStr;
	private String authTypeStr;

	public String getPhoneAuthStr() {
		return YesNoEnum.getName(super.getPhoneAuth());
	}

	public String getEmailAuthStr() {
		return YesNoEnum.getName(super.getEmailAuth());
	}

	public String getAuthTypeStr() {
		return UserAuthTypeEnum.getName(super.getAuthType());
	}
	private String phoneEmail;

	private String parentName;

	private String realName;
	private String  robotStr;

	public String getRobotStr() {
		return YesNoEnum.getName(super.getRobot());
	}
	private String  otcStr;

	public String getOtcStr() {
		return YesNoEnum.getName(super.getOtc());
	}


	private List<Long> ids;

	private Integer sendType;
}
