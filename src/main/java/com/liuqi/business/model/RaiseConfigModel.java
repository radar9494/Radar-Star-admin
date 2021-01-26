package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class RaiseConfigModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种
	 */
	
	private Long currencyId;
	
	/**
	 *币种图片
	 */
	
	private String image;
	
	/**
	 *rdb价格
	 */
	
	private BigDecimal rdbPrice;
	
	/**
	 *usdt价格
	 */
	
	private BigDecimal usdtPrice;
	
	/**
	 *目标人数
	 */
	
	private Integer targetNumber;
	
	/**
	 *发行时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date publishTime;
	
	/**
	 *发行总量
	 */
	
	private BigDecimal issuance;
	
	/**
	 *流通总量
	 */
	
	private BigDecimal circulation;
	
	/**
	 *白皮书
	 */
	
	private String whitePaper;
	
	/**
	 *官网
	 */
	
	private String url;
	
	/**
	 *区块查询
	 */
	
	private String block;
	
	/**
	 *简介
	 */
	
	private String introduction;
	
	/**
	 *显示状态
	 */
	
	private Integer showStatus;
	
	/**
	 *状态
	 */
	
	private Integer status;
	
	/**
	 *开始建
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;
	
	/**
	 *结束时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;

    /**
     * @Author 秦始皇188世
     * @Description 已购买数量
     * @Date 一购买 11:36
     * @Version 2020 Ultimate Edition
     */
	private BigDecimal buyTotal;

}
