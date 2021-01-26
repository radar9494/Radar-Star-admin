package com.liuqi.business.model;

import lombok.Data;

@Data
public class AddressModel {

	//未使用
	public static int STATUS_NO_USE=0;
	//已使用
	public static int STATUS_USE=1;
	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	//id
	private Long id;
	//路径
	private String path;
	//地址
	private String address;
	//状态  0未使用 1已使用
	private Integer status;

}
