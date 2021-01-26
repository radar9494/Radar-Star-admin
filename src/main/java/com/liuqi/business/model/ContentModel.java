package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class ContentModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *标题
	 */
	private String title;
	
	/**
	 *内容
	 */
	private String content;
	
	/**
	 *状态0不显示  1显示
	 */
	private Integer status;
	


}
