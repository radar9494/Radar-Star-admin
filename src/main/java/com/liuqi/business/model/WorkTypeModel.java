package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

@Data
public class WorkTypeModel extends BaseModel {

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *名称
	 */
	
	private String name;
	
	/**
	 *0显示 1不显示
	 */
	
	private Integer status;
	
	/**
	 *位置
	 */
	
	private Integer position;
	


}
