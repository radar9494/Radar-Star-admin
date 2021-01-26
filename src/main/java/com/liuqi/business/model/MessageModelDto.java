package com.liuqi.business.model;

import com.liuqi.business.enums.MessageTypeEnum;
import lombok.Data;
import com.liuqi.business.enums.MessageStatusEnum;

@Data
public class MessageModelDto extends MessageModel{

			
	private String statusStr;

    public String getStatusStr(){
    	return MessageStatusEnum.getName(super.getStatus());
	}

	private String typeStr;

	public String getTypeStr(){
		return MessageTypeEnum.getName(super.getStatus());
	}
	private String userName;
	private String realName;
}
