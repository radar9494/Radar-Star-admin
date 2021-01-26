package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

@Data
public class WorkDetailModel extends BaseModel {

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *工单号
	 */
	
	private Long workId;
	
	/**
	 *类型1用户 2系统
	 */
	
	private Integer type;
	
	/**
	 *内容
	 */
	
	private String content;
	
	/**
	 *图片1
	 */
	
	private String pic1;
	
	/**
	 *图片2
	 */
	
	private String pic2;

	/**
	 *图片3
	 */

	private String pic3;



}
