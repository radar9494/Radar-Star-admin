package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class RoleModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *角色名称
	 */
	private String name;

	/**
	 * 英文字符
	 */
	private String enName;


}
