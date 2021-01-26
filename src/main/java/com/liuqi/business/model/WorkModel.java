package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

@Data
public class WorkModel extends BaseModel {

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *工单内容
	 */
	
	private String title;
	
	/**
	 *工单号
	 */
	
	private String no;
	
	/**
	 *工单类型id
	 */
	
	private Long typeId;
	
	/**
	 *用户id
	 */
	
	private Long userId;
	
	/**
	 *手机
	 */
	
	private String phone;
	
	/**
	 *邮箱
	 */
	
	private String email;
	
	/**
	 *状态0未处理 1处理中 2完结
	 */
	
	private Integer status;


	/**
	 *状态0未解决 1已解决
	 */

	private Integer result;
}
