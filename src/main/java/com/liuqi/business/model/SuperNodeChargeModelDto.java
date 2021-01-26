package com.liuqi.business.model;

import lombok.Data;

import java.util.Date;

@Data
public class SuperNodeChargeModelDto extends SuperNodeChargeModel{


    private String  currencyName;
    private Date startDateStart;
    private Date  startDateEnd;


}
