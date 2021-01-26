package com.liuqi.business.service;

import com.liuqi.business.dto.MiningHomeDto;
import com.liuqi.business.dto.MiningIncomeDto;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Set;

/**
 * description: MiningUserTotalHandleServiceImpl <br> 用户持币总量
 * date: 2020/6/9 11:00 <br>
 * author: chenX <br>
 * version: 1.0 <br>
 */
public interface MiningUserTotalHandleService {

    void add(long userId, double num,Long currencyId);

    Long getQuantity(Long currencyId);

    Set<ZSetOperations.TypedTuple<String>> getAll(Long currencyId);

    double getTop(Long currencyId);

    Set<ZSetOperations.TypedTuple<String>> reverseRangeByScoreWithScores(long index, long end,Long currencyId);

    Long reverseRank(long userId,Long currencyId);

    void addPositionGain(long userId, double num,Long currencyId);

    void addPartnerIncome(long userId, double num);

    Double getPartnerIncome(long userId);

    void addPromotionRevenue(long userId, double num,Long currencyId);

    Double getPositionGain(long userId);

    Double getPromotionRevenue(long userId,Long currencyId);

    Double score(long userId,Long currencyId);

    void setMiningInfo(MiningHomeDto miningInfo);

    MiningHomeDto getMiningInfo(Long currencyId);

    void setDistribution(List<MiningIncomeDto> coordinate);

    List<MiningIncomeDto> getDistribution();

    void setCurrencyHoldingPower(long userId, double num);

    Double getCurrencyHoldingPower(long userId);

    void setPromotionOfComputingPower(long userId, double num);

    Double getPromotionOfComputingPower(long userId);

    void setYesterdaySRanking(String userId, Long num,Long currencyId);

    void clearYesterdaySRanking();

    Double getYesterdaySRanking(long userId,Long currencyId);

    void setTeamTotal(Long currencyId, int i,Long teamTotal,Long userId);

    Double getTeamTotal(Long currencyId, int i,Long userId);

    void setYesterdayComputing(Long k, double doubleValue, Long currencyId, int i);

    Double getYesterdayComputing(Long k, Long currencyId, int i);

}
