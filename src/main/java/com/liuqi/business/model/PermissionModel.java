package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class PermissionModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *菜单id
	 */
	private Long menuId;
	
	/**
	 *权限名称
	 */
	private String permissionName;
	
	/**
	 *按钮方法名称
	 */
	private String methodName;
	


}
