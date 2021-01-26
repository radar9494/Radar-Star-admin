package com.liuqi.business.model;

import lombok.Data;

@Data
public class AlertsModelDto extends AlertsModel{


    //给前台展示的时间
    private String showDate;

    //  月  日的时间格式
    private String cTime;


    //是否已点赞
    private Integer hasUpClick = 0;

    //是否已点踩
    private Integer hasDownClick = 0;

    //截取的内容
    private String subContent = "";

    //是否打开
    private boolean openStatus = false;
		
		

	


}
