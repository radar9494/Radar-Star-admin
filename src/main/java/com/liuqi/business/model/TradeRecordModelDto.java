package com.liuqi.business.model;

import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.WalletDoEnum;
import com.liuqi.business.enums.YesNoEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class TradeRecordModelDto extends TradeRecordModel{


	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	private String sellUserName;
	private String buyUserName;
	private String tradeTypeStr;
	private String currencyName;
	private String tradeCurrencyName;

	//显示类型
	private String typeShow;
	public String getTradeTypeStr() {
		return BuySellEnum.getName(super.getTradeType());
	}

	private String buyWalletStatusStr;
	private String sellWalletStatusStr;

	private boolean searchTradeList;
	private List<Long> tradeList;

	public String getBuyWalletStatusStr() {
		return WalletDoEnum.getName(super.getBuyWalletStatus());
	}

	public String getSellWalletStatusStr() {
		return WalletDoEnum.getName(super.getSellWalletStatus());
	}

	/**
	 * 生成订单明细表
	 * @param tradeId  交易对
	 * @param buyOrder 买单
	 * @param buyRateMoney 买单手续费
	 * @param sellOrder   卖单
	 * @param sellRateMoney 卖单手续费
	 * @param tradeQuantity 实际数量
	 * @param tradePrice  价格
	 * @return
	 */
	public static TradeRecordModelDto buildTradeRecordModel(Long tradeId,TrusteeModel buyOrder,BigDecimal buyRateMoney, TrusteeModel sellOrder,BigDecimal sellRateMoney,BigDecimal tradeQuantity,BigDecimal tradePrice,BigDecimal buyPrice,BigDecimal sellPrice,Integer tradeType,boolean robot){
		TradeRecordModelDto record=new TradeRecordModelDto();
		record.setTradeId(tradeId);
		record.setSellUserId(sellOrder.getUserId());
		record.setSellTrusteeId(sellOrder.getId());
		record.setBuyUserId(buyOrder.getUserId());
		record.setBuyTrusteeId(buyOrder.getId());
		record.setTradeQuantity(tradeQuantity);
		record.setTradePrice(tradePrice);
		record.setSellCharge(sellRateMoney);
		record.setBuyCharge(buyRateMoney);
		record.setBuyPrice(buyPrice);
		record.setSellPrice(sellPrice);
		record.setTradeType(tradeType);
		record.setRobot(robot?YesNoEnum.YES.getCode():YesNoEnum.NO.getCode());
		record.setBuyWalletStatus(WalletDoEnum.NOT.getCode());
		record.setSellWalletStatus(WalletDoEnum.NOT.getCode());
		return record;
	}

	/**
	 * 生成机器人订单明细表
	 * @param tradeId  交易
	 * @param tradeQuantity
	 * @param price
	 * @param tradeType
	 * @return
	 */
	public static TradeRecordModelDto buildRobotTradeRecordModel(Long tradeId, BigDecimal tradeQuantity, BigDecimal price, Integer tradeType){
		TradeRecordModelDto record=new TradeRecordModelDto();
		record.setTradeId(tradeId);
		record.setSellUserId(0L);
		record.setSellTrusteeId(0L);
		record.setBuyUserId(0L);
		record.setBuyTrusteeId(0L);
		record.setTradeQuantity(tradeQuantity);
		record.setTradePrice(price);
		record.setSellCharge(BigDecimal.ZERO);
		record.setBuyCharge(BigDecimal.ZERO);
		record.setBuyPrice(price);
		record.setSellPrice(price);
		record.setTradeType(tradeType);
		record.setRobot(YesNoEnum.YES.getCode());
		record.setBuyWalletStatus(WalletDoEnum.SUCCESS.getCode());
		record.setSellWalletStatus(WalletDoEnum.SUCCESS.getCode());
		return record;
	}

}
