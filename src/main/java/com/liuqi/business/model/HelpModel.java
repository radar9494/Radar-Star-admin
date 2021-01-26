package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

@Data
public class HelpModel extends BaseModel {

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *类型id
	 */
	
	private Long typeId;
	
	/**
	 *标题
	 */
	
	private String title;
	
	/**
	 *内容
	 */
	
	private String content;
	
	/**
	 *0不启用 1启用
	 */
	
	private Integer status;
	


}
