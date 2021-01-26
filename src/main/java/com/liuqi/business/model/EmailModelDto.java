package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liuqi.business.enums.UsingEnum;
import lombok.Data;
import java.util.Date;

@Data
public class EmailModelDto extends EmailModel{


	@JsonIgnore
	private String sortName="create_time desc,t.id";
	@JsonIgnore
	private String sortType="desc";


	private Integer using;

	private String statusStr;

	public String getStatusStr() {
		return UsingEnum.getName(super.getStatus());
	}
}
