package com.liuqi.business.model;

import lombok.Data;
import net.sf.saxon.style.DataElement;

import java.util.Date;

@Data
public class ServiceChargeModelDto extends ServiceChargeModel{

		private String currencyName;

		private Date calcDateStart;
		private Date calcDateEnd;

}
