package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class MnemonicWordModelDto extends MnemonicWordModel{

	@JsonIgnore
	private String sortName="create_time desc,t.id";

	@JsonIgnore
	private String sortType="desc";

}
