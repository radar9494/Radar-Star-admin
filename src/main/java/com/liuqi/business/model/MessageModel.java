package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;


@Data
public class MessageModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户id
	 */
	private Long userId;
	
	/**
	 *内容
	 */
	private String content;
	
	/**
	 *状态0未读  1已读
	 */
	private Integer status;

	/**
	 * 类型
	 */
	private Integer type;

}
