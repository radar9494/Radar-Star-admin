package com.liuqi.business.model;

import lombok.Data;

import java.util.Date;


@Data
public class KDataModelDto extends KDataModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	//开始时间
	private Date startTime;
	//结束时间
	private Date sendTime;

}
