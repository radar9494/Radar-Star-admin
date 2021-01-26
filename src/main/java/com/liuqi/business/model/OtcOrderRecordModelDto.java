package com.liuqi.business.model;

import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.OtcOrderRecordSuccessStatusEnum;
import lombok.Data;
import com.liuqi.business.enums.OtcOrderRecordStatusEnum;

import java.util.List;

@Data
public class OtcOrderRecordModelDto extends OtcOrderRecordModel{

									
	private String statusStr;

    public String getStatusStr(){
    	return OtcOrderRecordStatusEnum.getName(super.getStatus());
	}

	private String typeStr;

	public String getTypeStr() {
		return BuySellEnum.getName(super.getType());
	}


	private String appealTypeStr;

	public String getAppealTypeStr() {
		return BuySellEnum.getName(super.getAppealType());
	}
	private String successStatusStr;

	public String getSuccessStatusStr() {
		return OtcOrderRecordSuccessStatusEnum.getName(super.getSuccessStatus());
	}
	private String currencyName;
	private String buyUserName;
	private String sellUserName;
	private String appealUserName;
	private String buyOtcName;
	private String sellOtcName;
	private String appealOtcName;
	//显示交易类型 用户查询交易列表里面
	private String typeShow;
	List<Integer> statusList;
}
