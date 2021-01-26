package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class SlideModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *图片地址
	 */
	private String path;
	
	/**
	 *跳转链接
	 */
	private String hrefPath;
	
	/**
	 *位置
	 */
	private Integer position;
	
	/**
	 *跳转
	 */
	private Integer outHref;
	
	/**
	 *状态 0不显示 1显示
	 */
	private Integer status;

	/**
	 * 0PC  1APP
	 */
	private Integer type;

}
