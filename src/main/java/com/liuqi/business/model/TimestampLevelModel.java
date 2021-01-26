package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class TimestampLevelModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户id
	 */
	
	private Long userId;
	
	/**
	 *位置
	 */
	
	private Long parentId;
	
	/**
	 *树级别
	 */
	
	private Integer treeLevel;
	
	/**
	 *树
	 */
	
	private String treeInfo;
	


}
