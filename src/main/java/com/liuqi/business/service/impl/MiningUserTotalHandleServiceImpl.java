package com.liuqi.business.service.impl;

import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.MiningHomeDto;
import com.liuqi.business.dto.MiningIncomeDto;
import com.liuqi.business.dto.MiningRankDto;
import com.liuqi.business.service.MiningUserTotalHandleService;
import com.liuqi.redis.HashRepository;
import com.liuqi.redis.RedisRepository;
import com.liuqi.redis.ZSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * description: MiningUserTotalHandleServiceImpl <br> 用户持币数据
 * date: 2020/6/9 11:00 <br>
 * author: chenX <br>
 * version: 1.0 <br>
 */
@Service
public class MiningUserTotalHandleServiceImpl implements MiningUserTotalHandleService {


    private ZSetRepository<String, String> zSetRepository;
    private HashRepository<String, String, Double> hashRepository;
    private RedisRepository redisRepository;


    @Autowired
    public MiningUserTotalHandleServiceImpl(ZSetRepository<String, String> zSetRepository, HashRepository<String, String, Double> hashRepository, RedisRepository redisRepository) {
        this.zSetRepository = zSetRepository;
        this.hashRepository = hashRepository;
        this.redisRepository = redisRepository;
    }

    @Override
    public Long getQuantity( Long currencyId) {
        Set<ZSetOperations.TypedTuple<String>> all = this.getAll(currencyId);
          Long total=0L;
        for (ZSetOperations.TypedTuple<String> longTypedTuple : all) {
            total =total+ longTypedTuple.getScore().longValue();
        }
        return total;
    }

    /**
    * @Author 秦始皇188世
    * @Description 转入矿池
    * @Date 2020/7/13 18:02
    * @Version 2020 Ultimate Edition
    */
    @Override
    public void add(long userId, double num,Long currencyId) {
        zSetRepository.incrementScore(KeyConstant.KEY_USER_MINING_TOTAL+":"+currencyId, String.valueOf(userId), num);
    }

    /**
     * @Author 秦始皇188世
     * @Description 获取矿池持币量
     * @Date  18:02
     * @Version 2020 Ultimate Edition
     */
    @Override
    public Double score(long userId,Long currencyId) {
        Double score = zSetRepository.score(KeyConstant.KEY_USER_MINING_TOTAL+":"+currencyId, String.valueOf(userId));
        return score==null ? 0 : score;
    }


    public Double score(long[] userId,Long currencyId) {
        Double total=0.0;
        for(Long id:userId){
            Double score = zSetRepository.score(KeyConstant.KEY_USER_MINING_TOTAL+":"+currencyId, String.valueOf(id));
            total=total+score;
        }
        return total==null ? 0 : total;
    }

    /**
     * @Author 秦始皇188世
     * @Description 排名
     * @Date 2020/7/13 18:03
     * @Version 2020 Ultimate Edition
     */
    @Override
    public Long reverseRank(long userId,Long currencyId) {
        return zSetRepository.reverseRank(KeyConstant.KEY_USER_MINING_TOTAL+":"+currencyId, String.valueOf(userId));
    }

    /**
     * @Author 秦始皇188世
     * @Description
     * @Date 2020/7/13 18:03
     * @Version 2020 Ultimate Edition
     */
    @Override
    public Set<ZSetOperations.TypedTuple<String>> getAll(Long currencyId) {
        return zSetRepository.rangeWithScores(KeyConstant.KEY_USER_MINING_TOTAL+":"+currencyId, 0, -1);
    }

    /**
     * @Author 秦始皇188世
     * @Description 获取第一名
     * @Date 2020/7/13 18:03
     * @Version 2020 Ultimate Edition
     */
    @Override
    public double getTop(Long currencyId) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetRepository.reverseRangeByScoreWithScores(KeyConstant.KEY_USER_MINING_TOTAL+":"+currencyId, 0, 0);
        if (typedTuples != null && typedTuples.size() > 0) {
            return typedTuples.iterator().next().getScore();
        }
        return 0;
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> reverseRangeByScoreWithScores(long index,long end,Long currencyId) {
        return zSetRepository.reverseRangeByScoreWithScores(KeyConstant.KEY_USER_MINING_TOTAL+":"+currencyId, index, end);
    }

    /**
     * @Author 秦始皇188世
     * @Description 获取静态收益
     * @Date 2020/7/13 18:03
     * @Version 2020 Ultimate Edition
     */
    @Override
    public void addPositionGain(long userId, double num,Long currencyId) {
        hashRepository.increment(KeyConstant.KEY_USER_MINING_POSITION_TOTAL+":"+currencyId, String.valueOf(userId), num);
    }


    /**
     * @Author 秦始皇188世
     * @Description 获取动态收益
     * @Date 2020/7/13 18:03
     * @Version 2020 Ultimate Edition
     */
    @Override
    public Double getPositionGain(long userId) {
        Object num = hashRepository.get(KeyConstant.KEY_USER_MINING_POSITION_TOTAL, String.valueOf(userId));
        return num ==null ? 0 : Double.parseDouble(num.toString());
    }

    @Override
    public void addPartnerIncome(long userId, double num) {
        hashRepository.increment(KeyConstant.KEY_USER_MINING_PARTNER_INCOME_TOTAL, String.valueOf(userId), num);
    }
    @Override
    public Double getPartnerIncome(long userId) {
        Object num = hashRepository.get(KeyConstant.KEY_USER_MINING_PARTNER_INCOME_TOTAL, String.valueOf(userId));
        return num ==null ? 0 : Double.parseDouble(num.toString());
    }
    @Override
    public void addPromotionRevenue(long userId, double num,Long currencyId) {
        hashRepository.increment(KeyConstant.KEY_USER_MINING_TEAM_TOTAL+":"+currencyId, String.valueOf(userId), num);
    }

    /**
     *动态收益
     * @param userId
     * @return
     */
    @Override
    public Double getPromotionRevenue(long userId,Long currencyId) {
        Object num = hashRepository.get(KeyConstant.KEY_USER_MINING_TEAM_TOTAL+":"+currencyId, String.valueOf(userId));
        return num ==null ? 0 : Double.parseDouble(num.toString());
    }

    @Override
    public void setMiningInfo(MiningHomeDto miningInfo) {
        redisRepository.set(KeyConstant.KEY_MINING_DAILY_INFO+":"+miningInfo.getCurrencyId(), miningInfo);
    }

    @Override
    public MiningHomeDto getMiningInfo(Long currencyId) {
        return redisRepository.getModel(KeyConstant.KEY_MINING_DAILY_INFO+":"+currencyId);
    }

    @Override
    public void setDistribution(List<MiningIncomeDto> coordinateDto) {
        redisRepository.set(KeyConstant.KEY_MINING_DISTRIBUTION, coordinateDto);
    }

    @Override
    public List<MiningIncomeDto> getDistribution() {
        return redisRepository.getModel(KeyConstant.KEY_MINING_DISTRIBUTION);
    }




    @Override
    public void setCurrencyHoldingPower(long userId, double num) {
        hashRepository.set(KeyConstant.KEY_USER_MINING_CURRENCY_HOLDING_POWER, String.valueOf(userId), num);
    }

    @Override
    public Double getCurrencyHoldingPower(long userId) {
        Object num = hashRepository.get(KeyConstant.KEY_USER_MINING_CURRENCY_HOLDING_POWER, String.valueOf(userId));
        return num ==null ? 0 : Double.parseDouble(num.toString());
    }


    @Override
    public void setPromotionOfComputingPower(long userId, double num) {
        hashRepository.set(KeyConstant.KEY_USER_MINING_PROMOTION_COMPUTING_POWER, String.valueOf(userId), num);
    }

    @Override
    public Double getPromotionOfComputingPower(long userId) {
        Object num = hashRepository.get(KeyConstant.KEY_USER_MINING_PROMOTION_COMPUTING_POWER, String.valueOf(userId));
        return num ==null ? 0 : Double.parseDouble(num.toString());
    }

    @Override
    public void setYesterdaySRanking(String userId, Long num,Long currencyId) {
        hashRepository.set(KeyConstant.KEY_USER_MINING_YESTERDAY_RANKING+":"+currencyId, String.valueOf(userId), Double.valueOf(num));
    }

    @Override
    public void clearYesterdaySRanking() {
        redisRepository.del(KeyConstant.KEY_USER_MINING_YESTERDAY_RANKING);
    }

    @Override
    public void setTeamTotal(Long currencyId, int i,Long teamTotal,Long userId) {
        hashRepository.set(KeyConstant.KEY_USER_MINING_YESTERDAY_TEAM_TOTAL+currencyId+":"+i,userId+"",teamTotal.doubleValue());
    }

    @Override
    public Double getTeamTotal(Long currencyId, int i,Long userId) {
        //redisRepository.get(KeyConstant.KEY_USER_MINING_YESTERDAY_TEAM_TOTAL+currencyId+":"+i);
        Double aDouble = hashRepository.get(KeyConstant.KEY_USER_MINING_YESTERDAY_TEAM_TOTAL + currencyId + ":" + i, userId + "");
        return  aDouble==null?0:aDouble;
    }


    public Double getYesterdaySRanking(long userId,Long currencyId) {
        Object num = hashRepository.get(KeyConstant.KEY_USER_MINING_YESTERDAY_RANKING+":"+currencyId, String.valueOf(userId));
        return num ==null ? 0 : Double.parseDouble(num.toString());
    }

    @Override
    public Double getYesterdayComputing(Long k, Long currencyId, int i) {
      return   hashRepository.get(KeyConstant.KEY_USER_MINING_YESTERDAY_COMPUTING+currencyId+":"+i,k+"");
    }

    @Override
    public void setYesterdayComputing(Long k, double doubleValue, Long currencyId, int i) {
        hashRepository.set(KeyConstant.KEY_USER_MINING_YESTERDAY_COMPUTING+currencyId+":"+i,k+"",doubleValue);
    }
}
