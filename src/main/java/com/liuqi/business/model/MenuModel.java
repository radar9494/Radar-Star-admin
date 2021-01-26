package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class MenuModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *连接地址
	 */
	private String url;
	
	/**
	 *父id
	 */
	private Long parentId;
	
	/**
	 *菜单名称
	 */
	private String name;
	
	/**
	 *位置
	 */
	private Integer position;
	


}
