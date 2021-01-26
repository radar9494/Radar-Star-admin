package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

@Data
public class AlertsModel extends BaseModel {

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *状态 0显示 1不显示
	 */
	
	private Integer status;
	
	/**
	 *标题
	 */
	
	private String title;
	
	/**
	 *内容
	 */
	
	private String content;
	
	/**
	 *获取快讯的ID
	 */
	
	private Long aid;
	
	/**
	 *星级
	 */
	
	private Integer grade;
	
	/**
	 *是否标红 red就是
	 */
	
	private String highlightColor;
	
	/**
	 *快讯时间
	 */
	
	private Long createdAt;
	
	/**
	 *点赞数量
	 */
	
	private Integer upClickCount;
	
	/**
	 *点踩数量
	 */
	
	private Integer downClickCount;
	
	/**
	 *YYYY-MM-DD日期
	 */
	
	private String staticDate;
	


}
