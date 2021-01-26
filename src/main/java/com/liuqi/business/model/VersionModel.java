package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

@Data
public class VersionModel extends BaseModel {

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *安卓版本号
	 */
	
	private String androidVersion;
	
	/**
	 *下载地址
	 */
	
	private String androidAddress;
	
	/**
	 *更新内容
	 */
	
	private String androidInfo;
	
	/**
	 *安卓版本号
	 */
	
	private String iosVersion;
	
	/**
	 *下载地址
	 */
	
	private String iosAddress;
	
	/**
	 *更新内容
	 */
	
	private String iosInfo;
	


}
