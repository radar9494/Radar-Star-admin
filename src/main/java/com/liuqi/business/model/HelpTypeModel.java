package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

@Data
public class HelpTypeModel extends BaseModel {

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *父类id
	 */
	
	private Long parentId;
	
	/**
	 *名称
	 */
	
	private String name;
	
	/**
	 *位置
	 */
	
	private Integer position;
	
	/**
	 *0不启用 1启用
	 */
	
	private Integer status;
	


}
