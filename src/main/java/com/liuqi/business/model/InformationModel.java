package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class InformationModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *资讯id
	 */
	
	private Long infoId;
	
	/**
	 *标题
	 */
	
	private String title;
	
	/**
	 *内容
	 */
	
	private String content;
	
	/**
	 *发表时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date pubTime;
	
	/**
	 *看涨数量
	 */
	
	private Integer upCounts;
	
	/**
	 *看跌数量
	 */
	
	private Integer downCounts;
	


}
