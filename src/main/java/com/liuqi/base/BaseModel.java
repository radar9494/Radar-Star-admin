package com.liuqi.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseModel implements Serializable{
	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	/*编号*/
	private Long id;
	/*创建时间*/
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date createTime;
	/*更新时间*/
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date updateTime;
	/*备注*/
	private String remark;
	/*乐观锁 */
	@JsonIgnore
	private Integer version;

	/**
	 * 查询字段
	 */
	private Date startCreateTime;
	/**
	 * 查询字段
	 */
	private Date endCreateTime;
	@JsonIgnore
	private String sortName="create_time desc,t.id";
	@JsonIgnore
	private String sortType="desc";

}
