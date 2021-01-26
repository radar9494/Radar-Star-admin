package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;

@Data
public class ListingApplyModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *联系人手机号
	 */

	@NotNull(message="手机号不能为空")
	private String phone;
	
	/**
	 *联系人姓名
	 */
	@NotNull(message="姓名不能为空")
	private String realName;
	
	/**
	 *币种中文名称
	 */
	@NotNull(message="币种中文不能为空")
	private String currencyNameCn;
	
	/**
	 *币种英文名称
	 */
	@NotNull(message="币种英文不能为空")
	private String currencyNameEn;
	
	/**
	 *总发现量
	 */
	@NotNull(message="总发现量不能为空")
	private String total;
	
	/**
	 *市场已流通量
	 */
	@NotNull(message="市场已流通量不能为空")
	private String liquidity;
	
	/**
	 *社区用户量
	 */
	@NotNull(message="社区用户量不能为空")
	private String communityCount;
	
	/**
	 *营销预算
	 */
	@NotNull(message="营销预算不能为空")
	private String marketingBudget;
	
	/**
	 *项目介绍
	 */
	@NotNull(message="项目介绍不能为空")
	private String projectIntroduction;
	
	/**
	 *状态
	 */
	
	private Integer status;
	


}
