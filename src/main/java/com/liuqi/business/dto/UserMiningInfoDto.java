package com.liuqi.business.dto;

import lombok.Builder;
import lombok.Data;

/**
 * description: UserMiningInfoDto <br>
 * date: 2020/6/16 9:58 <br>
 * author: chenX <br>
 * version: 1.0 <br>
 */
@Data
@Builder
public class UserMiningInfoDto {

    private String userName;
    private double score;
    private double positionGain;
    private double directPushTotal;
    private double teamPushTotal;
    private double promotionRevenue;
    private double currencyHoldingPower;
    private double promotionOfComputingPower;
    private double yesterdayRank;
    private double nowRank;
    private double partnerIncome;

}
