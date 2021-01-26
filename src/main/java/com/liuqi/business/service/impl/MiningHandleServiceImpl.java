package com.liuqi.business.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONPOJOBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.liuqi.business.dto.MiningHomeDto;
import com.liuqi.business.dto.MiningIncomeDto;
import com.liuqi.business.dto.MiningRankDto;
import com.liuqi.business.dto.MiningSubDto;
import com.liuqi.business.enums.MiningWalletLogEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.jobtask.MiningJob;
import com.liuqi.response.ReturnResponse;
import com.liuqi.third.zb.SearchPrice;
import com.liuqi.utils.MathUtil;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.apache.velocity.runtime.directive.Break;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.swing.text.AbstractDocument;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * description: MiningHandleServiceImpl <br>
 * date: 2020/6/9 11:00 <br>
 * author: chenX <br>
 * version: 1.0 <br>
 */
@Service
@Slf4j
public class MiningHandleServiceImpl implements MiningHandleService {

    private MiningLogService miningLogService;
    private UserWalletService userWalletService;
    private UserWalletLogService userWalletLogService;
    private CurrencyService currencyService;
    private MiningConfigService miningConfigService;
    private MiningUserTotalHandleService miningUserTotalHandleService;
    private UserLevelService userLevelService;
    private MiningIncomeLogService miningIncomeLogService;
    private MiningWalletService miningWalletService;
    private TaskService taskService;
    private UserService userService;
    private MiningWalletLogService miningWalletLogService;
    private TradeService tradeService;
    private SearchPrice searchPrice;
    private ConfigService configService;


    @Autowired
    public MiningHandleServiceImpl(MiningLogService miningLogService, UserWalletService userWalletService, UserWalletLogService userWalletLogService, CurrencyService currencyService, MiningConfigService miningConfigService, MiningUserTotalHandleService miningUserTotalHandleService, UserLevelService userLevelService, MiningIncomeLogService miningIncomeLogService, MiningWalletService miningWalletService, TaskService taskService
    ,UserService userService,MiningWalletLogService miningWalletLogService,TradeService tradeService,SearchPrice searchPrice,ConfigService configService) {
        this.miningLogService = miningLogService;
        this.userWalletService = userWalletService;
        this.userWalletLogService = userWalletLogService;
        this.currencyService = currencyService;
        this.miningConfigService = miningConfigService;
        this.miningUserTotalHandleService = miningUserTotalHandleService;
        this.userLevelService = userLevelService;
        this.miningIncomeLogService = miningIncomeLogService;
        this.miningWalletService = miningWalletService;
        this.taskService = taskService;
        this.userService=userService;
        this.miningWalletLogService=miningWalletLogService;
        this.tradeService=tradeService;
        this.searchPrice=searchPrice;
        this.configService=configService;
    }

    @Override
    public ReturnResponse config(long userId,Long currencyId) {
        MiningConfigModel config = miningConfigService.findConfig(0,currencyId);
        UserWalletModelDto byUserAndCurrencyId = userWalletService.getByUserAndCurrencyId(userId, currencyId);
        return ReturnResponse.backSuccess(ImmutableMap.of("config", config, "using", byUserAndCurrencyId.getUsing()));
    }

    /**
     * 首页上方数据
     *
     * @param userId
     * @return
     */
    @Override
    public ReturnResponse home(long userId) {

        MiningConfigModelDto search=new MiningConfigModelDto();
        search.setType(0);
        List<MiningConfigModelDto > list=miningConfigService.queryListByDto(search,false);
        List<MiningHomeDto> homeList=new ArrayList<>();
        JSONObject obj=new JSONObject();
         BigDecimal totalRdt=BigDecimal.ZERO;
        BigDecimal  dynamicRdt=BigDecimal.ZERO;
        BigDecimal  timestampRdt=BigDecimal.ZERO;
        BigDecimal  freezeRdt=BigDecimal.ZERO;
         Long rdbId=currencyService.getRdtId();
        BigDecimal rdbPrice=tradeService.getPriceByCurrencyId(rdbId);
        for(MiningConfigModelDto item:list){
            MiningHomeDto miningInfo = miningUserTotalHandleService.getMiningInfo(item.getCurrencyId());
            CurrencyModel currencyModel=currencyService.getById(item.getCurrencyId());
           if(miningInfo==null){
               miningInfo=new MiningHomeDto();
           }
            miningInfo.setImage(currencyModel.getPic());
            miningInfo.setCurrencyName(currencyModel.getName());
            miningInfo.setOutQuantity(item.getOutQuantity());
            miningInfo.setCurrencyId(item.getCurrencyId());
            if (miningInfo == null) {
                miningInfo = new MiningHomeDto();
            }
            if(item.getWorst()!=null&&item.getWorst().compareTo(BigDecimal.ZERO)>0){
                miningInfo.setWorstHoldingCurrency(item.getWorst().longValue());
            }
            if(item.getBest()!=null&&item.getBest().compareTo(BigDecimal.ZERO)>0){
                miningInfo.setBestHoldingCurrency(item.getBest().longValue());
            }
            BigDecimal staticQuantity=miningIncomeLogService.getTotalByType(userId,0,item.getCurrencyId());
            BigDecimal dynamicQuantity=miningIncomeLogService.getTotalByType(userId,1,item.getCurrencyId());
            //时间戳
            BigDecimal timestampQuantity=miningIncomeLogService.getTotalByType(userId,2,item.getCurrencyId());
            BigDecimal total=staticQuantity.add(timestampQuantity).add(dynamicQuantity);
           BigDecimal yesteartDayTotal=miningIncomeLogService.yesteartDayTotal(userId,item.getCurrencyId());

            miningInfo.setStaticQuantity(staticQuantity);
            miningInfo.setTimestampQuantity(timestampQuantity);
            miningInfo.setDynamicQuantity(dynamicQuantity);
            miningInfo.setTotal(total);
            MiningWalletModel model=miningWalletService.findByUserIdAndCurrencyId(userId,item.getCurrencyId());
            miningInfo.setFreeze(model.getFreeze());
            if(item.getCurrencyId().equals(rdbId)){
                freezeRdt=freezeRdt.add(model.getFreeze());
                dynamicRdt=dynamicRdt.add(dynamicQuantity);
                totalRdt=totalRdt.add(yesteartDayTotal);
                timestampRdt=timestampRdt.add(timestampQuantity);
            }else{
                BigDecimal usdtPrice=tradeService.getPriceByCurrencyId(item.getCurrencyId());
                dynamicRdt=dynamicRdt.add(MathUtil.div(dynamicQuantity.multiply(usdtPrice),rdbPrice));
                totalRdt=totalRdt.add(MathUtil.div(yesteartDayTotal.multiply(usdtPrice),rdbPrice));
                timestampRdt=timestampRdt.add(MathUtil.div(timestampQuantity.multiply(usdtPrice),rdbPrice));
                freezeRdt=freezeRdt.add(MathUtil.div(model.getFreeze().multiply(usdtPrice),rdbPrice));
            }
            homeList.add(miningInfo);
        }
        obj.put("dynamicRdt",dynamicRdt);
        obj.put("totalRdt",totalRdt);
        obj.put("timestampRdt",timestampRdt);
        obj.put("homeList",homeList);
        obj.put("freezeRdt",freezeRdt);
        return ReturnResponse.backSuccess(obj);
    }

    @Override
    public ReturnResponse distribution() {
        return ReturnResponse.backSuccess(miningUserTotalHandleService.getDistribution());
    }

    /**
     * 近七日收益图
     *
     * @param userId
     * @return
     */
    @Override
    public ReturnResponse earningsGraph(long userId) {
        return ReturnResponse.backSuccess(ImmutableMap.of("incomeFromHoldingCurrency", miningIncomeLogService.sevenDay(userId, (byte) 2),
                "promotionRevenue", miningIncomeLogService.sevenDay(userId, (byte) 1)
        ));
    }

    /**
     * 挖矿转入
     *
     * @param userId
     * @param num
     * @return
     */
    @Override
    @Transactional
    public ReturnResponse transfer(long userId, BigDecimal num,Long currencyId) {
        if(num.compareTo(BigDecimal.ZERO)<0){
            throw new BusinessException("数量异常!");
        }
        MiningConfigModel config = miningConfigService.findConfig(0,currencyId);
        Assert.isTrue(num != null && num.compareTo(config.getTransferMin()) >= 0, "低于最小转入数量:"+config.getTransferMin());
        MiningWalletModel modified = miningWalletService.modified(userId, config.getCurrencyId(), num.negate(), num);
        MiningLogModel miningLogModel = miningLogService.addLog(userId, num, config.getCurrencyId(), currencyService.getNameById(config.getCurrencyId()), (byte) 0);
        miningWalletLogService.addLog(userId, config.getCurrencyId(), num.negate(),WalletLogTypeEnum.MINING_IN.getCode(),"矿池转入" ,modified);
        miningUserTotalHandleService.add(miningLogModel.getUserId(), miningLogModel.getNum().doubleValue(),config.getCurrencyId());
        // addTask(miningLogModel.getId());
        return ReturnResponse.backSuccess();
    }

    /**
     * 挖矿转出
     *
     * @param userId
     * @param num
     * @return
     */
    @Override
    @Transactional
    public ReturnResponse rollOut(long userId, BigDecimal num,Long currencyId) {
        MiningConfigModel config = miningConfigService.findConfig(0,currencyId);
        if(num.compareTo(config.getOutMin()) < 0){
            throw new BusinessException("低于最小转出数量");
        }
        Assert.isTrue(num != null && num.compareTo(config.getOutMin()) >= 0, "数量异常");
        Double score = miningUserTotalHandleService.score(userId,currencyId);
        Assert.isTrue(score != null && score >= num.doubleValue(), "矿池数量不足");
        MiningLogModel miningLogModelDto = miningLogService.addLog(userId, num, config.getCurrencyId(), currencyService.getNameById(config.getCurrencyId()), (byte) 1);
        MiningWalletModel modified = miningWalletService.modified(miningLogModelDto.getUserId(), miningLogModelDto.getCurrencyId(), miningLogModelDto.getNum(), miningLogModelDto.getNum().negate());
        miningWalletLogService.addLog(userId, config.getCurrencyId(), num,MiningWalletLogEnum.OUT.getCode(),"矿池转出", modified);
      //  addTask(miningLogModel.getId());
       Long usdtId=currencyService.getUsdtId();
       //扣usdt手续费
        UserWalletModelDto wallet = userWalletService.getByUserAndCurrencyId(userId, usdtId);
         if(wallet.getUsing().compareTo(config.getRedemptionUsdt())<0){
             throw new BusinessException("USDT不足!");
         }
        wallet.setUsing(wallet.getUsing().subtract(config.getRedemptionUsdt()));
        userWalletService.update(wallet);
        userWalletLogService.addLog(userId,usdtId,config.getRedemptionUsdt().negate(),WalletLogTypeEnum.MINING_REDEMPTION.getCode(),
                null,WalletLogTypeEnum.MINING_REDEMPTION.getName(),wallet);
        miningUserTotalHandleService.add(userId, -num.doubleValue(),currencyId);
        return ReturnResponse.backSuccess();
    }

    @Override
    public ReturnResponse withdrawInfo(long userId,Long currencyId) {
        //MiningConfigModel config = miningConfigService.findConfig(0);
        return ReturnResponse.backSuccess(miningWalletService.findByUserIdAndCurrencyId(userId, currencyId));
    }

    @Override
    @Transactional
    public ReturnResponse withdraw(long userId, BigDecimal num,Long currencyId) {
//        Assert.isTrue(num != null && num.compareTo(BigDecimal.ZERO) > 0, "提币金额异常");
//        MiningConfigModel config = miningConfigService.findConfig(0,currencyId);
//        miningWalletService.modified(userId, config.getCurrencyId(), num.negate());
//        UserWalletModel userWalletModel = userWalletService.modifyWalletUsing(userId, config.getCurrencyId(), num);
//        userWalletLogService.addLog(userId, config.getCurrencyId(), num, WalletLogTypeEnum.MINING.getCode(), 0L, "挖矿收益提出", userWalletModel);
        return ReturnResponse.backSuccess();
    }

    /**
     * 24小时到账
     *
     * @param miningLog
     */
    private void addTask(long miningLog) {
        taskService.addDelayedJob(MiningJob.class.getName(), "miningJob-" + miningLog,
                "24小时任务", 1440, ImmutableMap.of("id", miningLog));
    }


    /**
     * 挖矿记录
     *
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ReturnResponse miningLog(long userId, int pageNum, int pageSize) {
        MiningLogModelDto miningLogModelDto = new MiningLogModelDto();
        miningLogModelDto.setUserId(userId);
        return ReturnResponse.backSuccess(miningLogService.queryFrontPageByDto(miningLogModelDto, pageNum, pageSize));
    }

    /**
     * 矿机收益统计
     *
     * @param userId
     * @return
     */
    @Override
    public ReturnResponse miningIncomeInfo(long userId,Long currencyId) {
        Double positionGain = miningUserTotalHandleService.getPositionGain(userId);
        Double promotionRevenue = miningUserTotalHandleService.getPromotionRevenue(userId,currencyId);
        Double partnerIncome = miningUserTotalHandleService.getPartnerIncome(userId);
        return ReturnResponse.backSuccess(ImmutableMap.of("positionGain", positionGain == null ? 0 : positionGain,
                "partnerIncome", partnerIncome == null ? 0 : partnerIncome,
                "promotionRevenue", promotionRevenue == null ? 0 : promotionRevenue));
    }

    /**
     * 收益记录
     *
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ReturnResponse miningIncomeLog(long userId, Long currencyId, int pageNum, int pageSize) {
        MiningIncomeLogModelDto miningLogModelDto = new MiningIncomeLogModelDto();
        miningLogModelDto.setUserId(userId);
        miningLogModelDto.setCurrencyId(currencyId);
        JSONObject obj=new JSONObject();

        return ReturnResponse.backSuccess(miningIncomeLogService.queryFrontPageByDto(miningLogModelDto, pageNum, pageSize));
    }


    /**
     * 获取当前排行前51名
     */
//    private void yesterdaySRanking() {
//        Set<ZSetOperations.TypedTuple<String>> typedTuples = miningUserTotalHandleService.reverseRangeByScoreWithScores(0, 50);
//        int rank = 1;
//        miningUserTotalHandleService.clearYesterdaySRanking();
//        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
//            miningUserTotalHandleService.setYesterdaySRanking(typedTuple.getValue(), rank++);
//        }
//    }


    /**
     * 每日计算动态分布图
     *
     * @param rateOfReturn
     * @param bestNum
     */
    private void normalDistribution(Map<Long, BigDecimal> rateOfReturn, Map<Long, Long> ranks, long bestNum) {
        Set<Long> longs = rateOfReturn.keySet();
        //持币人数总量
        long total = longs.size();
        List<MiningIncomeDto> k = Lists.newArrayList();
        if (total <= 9) {
            k.add(MiningIncomeDto.builder().x(0).y(0).build());
            for (Long aLong : longs) {
                MiningIncomeDto build = MiningIncomeDto.builder().x(aLong).y(rateOfReturn.get(aLong).doubleValue()).build();
                if (!k.contains(build)) {
                    k.add(build);
                }
            }
        } else {
            long l = total / 7;
            for (long i = 1; i <= 7; i++) {
                Long aLong = ranks.get(i * l);
                MiningIncomeDto build = MiningIncomeDto.builder().x(aLong).y(rateOfReturn.get(aLong).doubleValue()).build();
                if (!k.contains(build)) {
                    k.add(build);
                }
            }
            k.add(MiningIncomeDto.builder().x(0).y(0).build());
            MiningIncomeDto best = MiningIncomeDto.builder().x(bestNum).y(rateOfReturn.get(bestNum).doubleValue()).build();
            if (!k.contains(best)) {
                k.add(best);
            }
            Long aLong = ranks.get(total);
            MiningIncomeDto last = MiningIncomeDto.builder().x(aLong).y(rateOfReturn.get(aLong).doubleValue()).build();
            if (!k.contains(last)) {
                k.add(last);
            }
        }
        k.sort(Comparator.comparing(MiningIncomeDto::getX));
        miningUserTotalHandleService.setDistribution(k);
    }


    @Override
    public ReturnResponse getTotal(long userId) {
        return ReturnResponse.backSuccess(miningIncomeLogService.getTotal(userId));
    }

    @Override
    public ReturnResponse getUsing(long userId, Long currencyId) {
        JSONObject obj=new JSONObject();
        MiningWalletModel wallet = miningWalletService.findByUserIdAndCurrencyId(userId, currencyId);
        obj.put("using",wallet.getUsing());
        obj.put("freeze",wallet.getFreeze());
        BigDecimal price = tradeService.getPriceByCurrencyId(currencyId);
        BigDecimal usdtPrice = searchPrice.getUsdtQcPrice();
        obj.put("price",price);
        obj.put("usdtPrice",usdtPrice);
        return ReturnResponse.backSuccess(obj);
    }

    @Override
    public ReturnResponse miningIncomeTotal(long userId,Integer type) {
      List<MiningIncomeLogModel> list=  miningIncomeLogService.findSumByUserId(userId,type);
        BigDecimal rdbQuantity=BigDecimal.ZERO;
        BigDecimal cnyQuantity=BigDecimal.ZERO;
        if(CollectionUtil.isNotEmpty(list)){
            Long rdbId=currencyService.getRdtId();

            BigDecimal rdbPrice=tradeService.getPriceByCurrencyId(rdbId);
            BigDecimal usdtPrice=searchPrice.getUsdtQcPrice();
          for(MiningIncomeLogModel item:list){
              if(item.getCurrencyId().equals(rdbId)){
                  rdbQuantity=rdbQuantity.add(item.getNum());
              }else{
                  rdbQuantity=rdbQuantity.add(MathUtil.div(item.getNum().multiply(tradeService.getPriceByCurrencyId(item.getCurrencyId())),rdbPrice));
              }

          }
           cnyQuantity=rdbQuantity.multiply(rdbPrice).multiply(usdtPrice);
        }
       JSONObject obj=new JSONObject();
        obj.put("rdbQuantity",rdbQuantity);
        obj.put("cnyQuantity",cnyQuantity);
        return ReturnResponse.backSuccess(obj);
    }

    @Override
    public ReturnResponse currencyList() {
        MiningConfigModelDto search=new MiningConfigModelDto();
        search.setType(0);
        List<MiningConfigModelDto > list=miningConfigService.queryListByDto(search,true);
        return ReturnResponse.backSuccess(list);
    }

    @Override
    public ReturnResponse getList(long userId, Integer type,Long currencyId) {
        //获取所有直推
        List<Long> subs=null;
        if(type==0){
            subs=userLevelService.getAssignSubIdList(userId,1);
        }else{
            subs=userService.getTimestamSub(userId);
        }
        List<MiningSubDto> list=new ArrayList<>();
        for(Long id:subs){
            MiningSubDto model=new MiningSubDto();
            model.setName(userService.getNameById(id));
            Double score = miningUserTotalHandleService.score(id, currencyId);
            model.setQuantity(score);
            model.setTeamQuantity(miningUserTotalHandleService.getTeamTotal(currencyId,type,id));
            model.setComputing(miningUserTotalHandleService.getYesterdayComputing(id,currencyId,type));
            list.add(model);
        }
        return ReturnResponse.backSuccess(list);
    }

    @Override
    public ReturnResponse init(long userId) {
       JSONObject obj=new JSONObject();
       BigDecimal staticQuantity=miningIncomeLogService.getTotalByType(userId,0);
        BigDecimal dynamicQuantity=miningIncomeLogService.getTotalByType(userId,1);
        //时间戳
        BigDecimal timestampQuantity=miningIncomeLogService.getTotalByType(userId,2);
        obj.put("staticQuantity",staticQuantity);
        obj.put("dynamicQuantity",dynamicQuantity);
        obj.put("timestampQuantity",timestampQuantity);
        obj.put("total",timestampQuantity.add(staticQuantity).add(dynamicQuantity));
        return ReturnResponse.backSuccess(obj);
    }

    /**
     * 个人持币算力奖励每日发放    计算排名
     */
    public void income(Long currencyId) {
        MiningConfigModel config = miningConfigService.findConfig(0,currencyId);
        //yesterdaySRanking();
        Set<ZSetOperations.TypedTuple<String>> all = miningUserTotalHandleService.getAll(currencyId);
        long userRank = 0, allRank = 0, lastScore = 0, num = 0;
        Map<Long, MiningRankDto> userRankMap = Maps.newLinkedHashMap();
        for (ZSetOperations.TypedTuple<String> longTypedTuple : all) {
            //低于最低持币不算排名
            if(new BigDecimal(longTypedTuple.getScore()).compareTo(config.getWorst())<0){
                userRankMap.put(Long.valueOf(longTypedTuple.getValue()), MiningRankDto.builder().rank(0L).total(longTypedTuple.getScore().longValue()).build());
                continue;
            }
            num++;
            if (longTypedTuple.getScore().longValue() != lastScore) {
                lastScore = longTypedTuple.getScore().longValue();
                userRank = num;
            }
            allRank += userRank;
            userRankMap.put(Long.valueOf(longTypedTuple.getValue()), MiningRankDto.builder().rank(userRank).total(longTypedTuple.getScore().longValue()).build());
            miningUserTotalHandleService.setYesterdaySRanking(longTypedTuple.getValue(),userRank,currencyId);
            UserModel user=userService.getById(Long.parseLong(longTypedTuple.getValue()));
//            if(user.getRank()!=userRank){
//                user.setRank(userRank);
//                userService.update(user);
//            }
        }
        //最差
         BigDecimal best = BigDecimal.ZERO, worst = BigDecimal.ZERO;
        long bestNum = 0, worstNum = 0;
        String currencyName = currencyService.getNameById(config.getCurrencyId());
        Map<Long, BigDecimal> rateOfReturn = Maps.newLinkedHashMap();
        Map<Long, Long> ranks = Maps.newLinkedHashMap();
        long rank = 1;


        for (Map.Entry<Long, MiningRankDto> longMiningRankDtoEntry : userRankMap.entrySet()) {
                //排名为0跳过
               if(longMiningRankDtoEntry.getValue().getRank()==0){
                   continue;
               }
                BigDecimal personalComputingPower = MathUtil.div(BigDecimal.valueOf(longMiningRankDtoEntry.getValue().getRank()), BigDecimal.valueOf(allRank));
                miningUserTotalHandleService.setCurrencyHoldingPower(longMiningRankDtoEntry.getKey(), personalComputingPower.doubleValue());
                BigDecimal multiply = personalComputingPower.multiply(MathUtil.mul(config.getCardinality(), config.getStaticRate()));
               //发放静态收益
                userIncome(longMiningRankDtoEntry.getKey(), multiply, config.getCurrencyId(), currencyName);
                //收益率
               BigDecimal div = MathUtil.div(multiply, BigDecimal.valueOf(longMiningRankDtoEntry.getValue().getTotal()));
               rateOfReturn.put(longMiningRankDtoEntry.getValue().getTotal(), div);
               ranks.put(rank++, longMiningRankDtoEntry.getValue().getTotal());
                if (div.compareTo(best) > 0) {
                    best = div;
                    bestNum = longMiningRankDtoEntry.getValue().getTotal();
                }
                if (div.compareTo(worst) < 0 || worst.compareTo(BigDecimal.ZERO) == 0) {
                    worst = div;
                    worstNum = longMiningRankDtoEntry.getValue().getTotal();
                }
        }
        //normalDistribution(rateOfReturn, ranks, bestNum);
        //动态算力
        promotionRevenue(userRankMap, config);
        //时间戳动态
          config = miningConfigService.findConfig(1,currencyId);
        timestamPpromotionRevenue(userRankMap, config);
        miningUserTotalHandleService.setMiningInfo(MiningHomeDto.builder().bestHoldingCurrency(bestNum).worstHoldingCurrency(worstNum).currencyId(currencyId).build());
    }

    private JSObject calc(Map<Long, MiningRankDto> userRankMap, MiningConfigModel config) {
//
//        BigDecimal best,bestNum,worst,worstNum;
//        for (Map.Entry<Long, MiningRankDto> longMiningRankDtoEntry : userRankMap.entrySet()) {
//            BigDecimal personalComputingPower = MathUtil.div(BigDecimal.valueOf(longMiningRankDtoEntry.getValue().getRank()), BigDecimal.valueOf(allRank));
//            miningUserTotalHandleService.setCurrencyHoldingPower(longMiningRankDtoEntry.getKey(), personalComputingPower.doubleValue());
//            BigDecimal multiply = personalComputingPower.multiply(MathUtil.div(config.getCardinality(), config.getStaticRate()));
//            //发放静态收益
//            userIncome(longMiningRankDtoEntry.getKey(), multiply, config.getCurrencyId(), currencyName);
//            //收益率
//            BigDecimal div = MathUtil.div(multiply, BigDecimal.valueOf(longMiningRankDtoEntry.getValue().getTotal()));
//            rateOfReturn.put(longMiningRankDtoEntry.getValue().getTotal(), div);
//            ranks.put(rank++, longMiningRankDtoEntry.getValue().getTotal());
//            if (div.compareTo(best) > 0) {
//                best = div;
//                bestNum = longMiningRankDtoEntry.getValue().getTotal();
//            }
//            if (div.compareTo(worst) < 0 || worst.compareTo(BigDecimal.ZERO) == 0) {
//                worst = div;
//                worstNum = longMiningRankDtoEntry.getValue().getTotal();
//            }
//        }
           return  null;

    }


    private void timestamPpromotionRevenue(Map<Long, MiningRankDto> userRankMap, MiningConfigModel config) {
        BigDecimal allUserComputingPower = BigDecimal.ZERO;
        Map<Long, BigDecimal> user = Maps.newHashMap();
        for (Map.Entry<Long, MiningRankDto> longMiningRankDtoEntry : userRankMap.entrySet()) {
           //获取 激活时间前5 后5

            Integer count=  Integer.parseInt(configService.queryValueByName("timestamp.count"));
            if(count<0){
                return;
            }

            List<Long> byParentId = userService.getTimestamSub(longMiningRankDtoEntry.getKey());
               if (byParentId != null && byParentId.size() > 0) {
                   List<Long> lineList = new ArrayList<>();
                   byParentId.forEach(e -> {
                       List<Long> byParentId1 = userService.getTimestamSub(e);
                       lineList.add(userRankMap.get(e) == null ? 0 : userRankMap.get(e).getTotal()); //直推
                       if(count==1){
                          return;
                       }
                       //间推
                       long lineTotal2 = byParentId1.stream().mapToLong(a -> userRankMap.get(a) == null ? 0 : userRankMap.get(a).getTotal()).sum();
                       lineList.add(lineTotal2);
                       if(count==2){
                           return;
                       }
                       byParentId1.forEach(e1 -> {
                           List<Long> byParentId2= userService.getTimestamSub(e1);
                           //第三代
                           long    lineTotal3 = byParentId2.stream().mapToLong(a -> userRankMap.get(a) == null ? 0 : userRankMap.get(a).getTotal()).sum();
                            if (lineTotal3 > 0) {
                               lineList.add(lineTotal3);
                           }
                      });
                   });
                   //lineList.stream().
                   Long teamtotal =0L;
                   if (lineList.size() > 0) {
                       teamtotal = lineList.stream().reduce(Long::sum).orElse(0L);
                       log.info("时间戳团队持币:{},用户id{}",teamtotal,longMiningRankDtoEntry.getKey());
                       miningUserTotalHandleService.setTeamTotal(config.getCurrencyId(),1,teamtotal,longMiningRankDtoEntry.getKey());
                       long max = Collections.max(lineList);
                       BigDecimal maxNum = BigDecimal.valueOf(Math.pow(max, (1 / 3d)));
                       lineList.remove(lineList.indexOf(max));
                       long total = lineList.stream().mapToLong(s -> s > 10000 ? 90000 + s : s * 10).sum();
                       BigDecimal add = BigDecimal.valueOf(total).add(maxNum);
                       allUserComputingPower = allUserComputingPower.add(add);
                       if(longMiningRankDtoEntry.getValue().getRank()>0){
                           user.put(longMiningRankDtoEntry.getKey(), add);
                       }
                   }
               }
        }
        timestamBonus(user, allUserComputingPower, config);
    }

    private void timestamBonus(Map<Long, BigDecimal> user, BigDecimal allUserComputingPower, MiningConfigModel miningConfigModel) {
        if (allUserComputingPower.compareTo(BigDecimal.ZERO) > 0) {
            String currencyName = currencyService.getNameById(miningConfigModel.getCurrencyId());
            user.forEach((k, v) -> {
                BigDecimal div = MathUtil.div(v, allUserComputingPower);
                log.info("用户:{},个人算力:{},全网算力{}",k,v.toPlainString(),allUserComputingPower.toPlainString());
                miningUserTotalHandleService.setYesterdayComputing(k, v.doubleValue(),miningConfigModel.getCurrencyId(),1);

                BigDecimal multiply = div.multiply(MathUtil.mul(miningConfigModel.getCardinality(),miningConfigModel.getDynamicRate() ));
                miningUserTotalHandleService.setPromotionOfComputingPower(k, div.doubleValue());
                MiningWalletModel modified = miningWalletService.modified(k, miningConfigModel.getCurrencyId(), multiply, BigDecimal.ZERO);
                miningIncomeLogService.addLog(k, multiply, miningConfigModel.getCurrencyId(), currencyName, (byte) 2);
                miningUserTotalHandleService.addPromotionRevenue(k, multiply.doubleValue(),miningConfigModel.getCurrencyId());
                miningWalletLogService.addLog(k,miningConfigModel.getCurrencyId(),multiply,MiningWalletLogEnum.TIMESTAMP.getCode(),"时间戳收益",modified);
            });
        }

    }

    /**
     * 计算奖励发放
     *
     * @param userId
     * @param allRank
     * @param userRank
     * @param cardinality
     * @param currencyId
     */
    private void userIncome(long userId, BigDecimal num, long currencyId, String currencyName) {
        log.info("静态收益发放:userId:{},数量:{}",userId,num);
        MiningWalletModel modified = miningWalletService.modified(userId, currencyId, num, BigDecimal.ZERO);
        miningIncomeLogService.addLog(userId, num, currencyId, currencyName, (byte) 0);
        miningUserTotalHandleService.addPositionGain(userId, num.doubleValue(),currencyId);
        miningWalletLogService.addLog(userId,currencyId,num, MiningWalletLogEnum.STATIC.getCode(),"持币收益",modified);
    }


    /**
     * 推广收益
     */
    public void promotionRevenue(Map<Long, MiningRankDto> userRankMap, MiningConfigModel config) {
        BigDecimal allUserComputingPower = BigDecimal.ZERO;
        Map<Long, BigDecimal> user = Maps.newHashMap();
        for (Map.Entry<Long, MiningRankDto> longMiningRankDtoEntry : userRankMap.entrySet()) {
//            if (longMiningRankDtoEntry.getValue().getTotal() >= 100) {
                List<Long> byParentId = userLevelService.findByParentId(longMiningRankDtoEntry.getKey());
                if (byParentId != null && byParentId.size() > 0) {
                    List<Long> lineList = new ArrayList<>();
                    byParentId.forEach(e -> {
                        List<Long> allSubIdList = userLevelService.getAllSubIdList(e);
                        long lineTotal = allSubIdList.stream().mapToLong(a -> userRankMap.get(a) == null ? 0 : userRankMap.get(a).getTotal()).sum();
                        if (lineTotal > 0) {
                            lineList.add(lineTotal);
                        }
                    });
                    Long teamtotal =0L;
                    if (lineList.size() > 0) {
                        teamtotal = lineList.stream().reduce(Long::sum).orElse(0L);
                        miningUserTotalHandleService.setTeamTotal(config.getCurrencyId(),0,teamtotal,longMiningRankDtoEntry.getKey());
                        long max = Collections.max(lineList);
                        BigDecimal maxNum = BigDecimal.valueOf(Math.pow(max, (1 / 3d)));
                        lineList.remove(lineList.indexOf(max));
                        long total = lineList.stream().mapToLong(s -> s > 10000 ? 90000 + s : s * 10).sum();
                        BigDecimal add = BigDecimal.valueOf(total).add(maxNum);
                        allUserComputingPower = allUserComputingPower.add(add);
                       if(longMiningRankDtoEntry.getValue().getRank()>0){
                           user.put(longMiningRankDtoEntry.getKey(), add);
                       }

                    }
                    
                }
//            }
        }
        bonus(user, allUserComputingPower, config);
    }

    //动态
    public void bonus(Map<Long, BigDecimal> user, BigDecimal allUserComputingPower, MiningConfigModel miningConfigModel) {
        if (allUserComputingPower.compareTo(BigDecimal.ZERO) > 0) {
            String currencyName = currencyService.getNameById(miningConfigModel.getCurrencyId());
            user.forEach((k, v) -> {
                BigDecimal div = MathUtil.div(v, allUserComputingPower);
                log.info("用户:{},个人算力:{},全网算力{}",k,v.toPlainString(),allUserComputingPower.toPlainString());
                miningUserTotalHandleService.setYesterdayComputing(k, v.doubleValue(),miningConfigModel.getCurrencyId(),0);

                BigDecimal multiply = div.multiply(MathUtil.mul(miningConfigModel.getCardinality(),miningConfigModel.getDynamicRate() ));
                miningUserTotalHandleService.setPromotionOfComputingPower(k, div.doubleValue());
                MiningWalletModel modified = miningWalletService.modified(k, miningConfigModel.getCurrencyId(), multiply, BigDecimal.ZERO);
                miningIncomeLogService.addLog(k, multiply, miningConfigModel.getCurrencyId(), currencyName, (byte) 1);
                miningWalletLogService.addLog(k,miningConfigModel.getCurrencyId(),multiply,MiningWalletLogEnum.DYNAMIC.getCode(),"推广收益",modified);
                miningUserTotalHandleService.addPromotionRevenue(k, multiply.doubleValue(),miningConfigModel.getCurrencyId());
            });
        }
    }

    /**
     * 转入转出24小时到账
     *
     * @param miningId
     */
    @Override
    public void taskHandle(long miningId) {
//        MiningLogModelDto miningLogModelDto = miningLogService.getById(miningId);
//        if (miningLogModelDto.getState() == 0) {
//            if (miningLogModelDto.getType() == 0) {
//                miningUserTotalHandleService.add(miningLogModelDto.getUserId(), miningLogModelDto.getNum().doubleValue(),miningLogModelDto.getCurrencyId());
//            }
//            if (miningLogModelDto.getType() == 1) {
//                UserWalletModel userWalletModel = userWalletService.modifyWalletUsing(miningLogModelDto.getUserId(), miningLogModelDto.getCurrencyId(), miningLogModelDto.getNum());
//                userWalletLogService.addLog(miningLogModelDto.getUserId(), miningLogModelDto.getCurrencyId(), miningLogModelDto.getNum(), WalletLogTypeEnum.MINING.getCode(), miningLogModelDto.getId(), "挖矿转出", userWalletModel);
//            }
//        }
    }
}
