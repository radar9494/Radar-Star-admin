package com.liuqi.business.model;

import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.OtcOrderCancelEnum;
import com.liuqi.business.enums.OtcOrderPayEnum;
import com.liuqi.business.enums.OtcOrderStatusEnum;
import com.liuqi.utils.MathUtil;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OtcOrderModelDto extends OtcOrderModel{

												
	private String statusStr;

    public String getStatusStr(){
    	return OtcOrderStatusEnum.getName(super.getStatus());
	}

	private String cancelStr;

	public String getCancelStr(){
		return OtcOrderCancelEnum.getName(super.getCancel());
	}

	private String typeStr;

	public String getTypeStr() {
		return BuySellEnum.getName(super.getType());
	}

	private String yhkStr;

	private String zfbStr;

	private String wxStr;

	public String getYhkStr() {
		return OtcOrderPayEnum.getName(super.getYhk());
	}

	public String getZfbStr() {
		return OtcOrderPayEnum.getName(super.getZfb());
	}

	public String getWxStr() {
		return OtcOrderPayEnum.getName(super.getWx());
	}
	//剩余数量
	private BigDecimal residue;

	public BigDecimal getResidue() {
		if (super.getQuantity() != null && super.getTradeQuantity() != null) {
			return MathUtil.sub(super.getQuantity(), super.getTradeQuantity());
		}
		return BigDecimal.ZERO;
	}
	private String currencyName;
	private String userName;
	private String otcName;
	private String realName;
	//总单数
	private Integer total;
	//成功单数
	private Integer success;
}
