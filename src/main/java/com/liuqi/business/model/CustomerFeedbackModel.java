package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class CustomerFeedbackModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户id
	 */
	
	private Long userId;
	
	/**
	 *联系人
	 */
	
	private String name;
	
	/**
	 *联系方式
	 */
	
	private String phone;
	
	/**
	 *反馈标题
	 */
	
	private String title;
	
	/**
	 *反馈内容
	 */
	
	private String content;
	
	/**
	 *状态
	 */
	
	private Integer status;
	


}
