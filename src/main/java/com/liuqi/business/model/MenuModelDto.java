package com.liuqi.business.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class MenuModelDto extends MenuModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	private String parentName;

	//权限
	private List<PermissionModel> permissions=new ArrayList<>();


	private String nameLike;
}
