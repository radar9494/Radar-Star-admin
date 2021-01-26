package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;


@Data
public class UserLevelModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户id
	 */
	private Long userId;
	
	/**
	 *上级id
	 */
	private Long parentId;
	
	/**
	 *层级树
	 */
	private Integer treeLevel;
	
	/**
	 *层级树信息
	 */
	private String treeInfo;
	


}
