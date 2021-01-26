package com.liuqi.business.model;

import lombok.Data;


@Data
public class LoggerModelDto extends LoggerModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	//（1.添加 2.更新,3删除 ，4 拒绝）
	public  final static int TYPE_ADD=1;
	public  final static int TYPE_UPDATE=2;
	public  final static int TYPE_DELETE=3;
	public  final static int TYPE_REFUSE=4;

	private String adminName;

	private String typeStr;

	private String contentLike;
	public String getTypeStr() {
		if(super.getType()!=null){
			if(super.getType()==TYPE_ADD){
				typeStr="系统";
			}else if(super.getType()==TYPE_UPDATE){
				typeStr="开关";
			}
		}
		return typeStr;
	}
	//更新
	private String className;
}
