package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;


@Data
public class FinancingIntroduceModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *配置id
	 */
	
	private Long configId;
	
	/**
	 *标题
	 */
	
	private String title;
	
	/**
	 *图片
	 */
	
	private String image;
	
	/**
	 *内容
	 */
	
	private String content;
	


}
