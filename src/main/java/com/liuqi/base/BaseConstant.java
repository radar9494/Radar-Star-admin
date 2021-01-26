package com.liuqi.base;

/**
 * 基础常量设置
 * @author tanyan
 * 2017-8-10 下午12:01:45
 */
public class BaseConstant{
	public static final String BASE_PROJECT="baseDemo";//项目名称

	//用户类型
	public static final String TYPE_ADMIN="admin";
	public static final String TYPE_SYS="sys";
	public static final String TYPE_USER="user";
	//session中存在的值
	public static final String ADMIN_USER_SESSION="curAdminUser:"+BASE_PROJECT+":";//管理用户
	public static final String ADMIN_USERID_SESSION="curAdminUserID:"+BASE_PROJECT+":";//管理用户
	public static final String ADMIN_USER_ROLE="curAdminRole:"+BASE_PROJECT+":";//管理用户
	public static final String ADMIN_USER_MENU="adminUserMenu:"+BASE_PROJECT+":";//管理用户

	public static final String USERID_SESSION="curUserID:"+BASE_PROJECT+":";//普通用户
	public static final String USER_SESSION="curUser:"+BASE_PROJECT+":";//普通用户

	public static final String SYS_USER_SESSION="curSysUser:"+BASE_PROJECT+":";//管理用户
	public static final String SYS_USERID_SESSION="curSysUserID:"+BASE_PROJECT+":";//管理用户
	public static final String SYS_USERID_CURRENCY_SESSION="curSysUserID:currency:"+BASE_PROJECT+":";//管理用户币种
	public static final String SYS_USERID_TRADE_SESSION="curSysUserID:trade:"+BASE_PROJECT+":";//管理用户交易对

	//是否验证图像二维码
	//cookie验证码名称
	public static final String KAPTCHA_NAME=BASE_PROJECT+"_JCAPTCHA";
	//cookie过期时间
	public static final Integer KAPTCHA_TIME=5*60;


	//使用request  token时的名称
	public static final String TOKEN_NAME="token";
	//使用token过期时间 分钟
	public static final Long TOKEN_SESSION_TIME = 60*24*3L;

	//密码加密次数
	public static final int PWD_COUNT=67;

	//sheet导出数量
	public static final int EXPORT_COUNT=50000;
}
