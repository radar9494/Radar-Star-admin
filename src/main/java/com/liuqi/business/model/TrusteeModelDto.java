package com.liuqi.business.model;

import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.TrusteeStatusEnum;
import com.liuqi.business.enums.YesNoEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class TrusteeModelDto extends TrusteeModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	private String userName;
	private String realName;
	private Long areaId;
	private String areaName;
	private Long currencyId;
	private String currencyName;
	private Long tradeCurrencyId;
	private String tradeCurrencyName;

	private String tradeTypeStr;
	private boolean searchTradeList;
	private List<Long> tradeList;
	private List<Integer> statusList;
	private boolean limitCount;
	private Integer count;
	private BigDecimal nums;//数量
	public String getTradeTypeStr() {
		return BuySellEnum.getName(super.getTradeType());
	}
	private String statusStr;

	public String getStatusStr() {
		return TrusteeStatusEnum.getName(super.getStatus());
	}

	private String whiteStr;

	public String getWhiteStr() {
		return YesNoEnum.getName(super.getWhite());
	}

	private String robotStr;

	public String getRobotStr() {
		return YesNoEnum.getName(super.getRobot());
	}
}
