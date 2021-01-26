package com.liuqi.jobtask;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.TradeInfoDto;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.model.RobotModel;
import com.liuqi.business.model.TrusteeModelDto;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.mq.TradeTopic;
import com.liuqi.redis.ValueRepository;
import com.liuqi.third.price.HBSearchPrice;
import com.liuqi.utils.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 机器人发布
 */
@Slf4j
public class RobotJob implements Job {

    @Autowired
    private RobotService robotService;
    @Autowired
    private TrusteeService trusteeService;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private TradeRecordService tradeRecordService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private HBSearchPrice hbSearchPrice;
    @Autowired
    private TradeInfoCacheService tradeInfoCacheService;
    @Autowired
    private ValueRepository<String, Integer> valueRepository;


    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            Long id = jobExecutionContext.getJobDetail().getJobDataMap().getLong("id");
            RobotModel robotModel = robotService.getById(id);
            CurrencyTradeModelDto trade = currencyTradeService.getById(robotModel.getTradeId());
            Assert.isTrue(SwitchEnum.ON.getCode().equals(trade.getTradeSwitch()), trade.getId() + ",未开放交易");
            JSONArray ask = null, bid = null;
            if (robotModel.getRobotType().equals(1) && robotModel.getMainstream().equals((byte) 1)) {
                    JSONObject http = hbSearchPrice.getHttp(trade.getTradeCurrencyName(), trade.getCurrencyName());
                    ask = http.getJSONArray("ask");
                    bid = http.getJSONArray("bid");
            }
            if (SwitchEnum.isOn(robotModel.getBuySwitch())) {
                this.robotPublish(robotModel, BuySellEnum.BUY.getCode(), bid);
            }
            if (SwitchEnum.isOn(robotModel.getSellSwitch())) {
                this.robotPublish(robotModel, BuySellEnum.SELL.getCode(), ask);
            }
        } catch (Exception e) {
          //  log.error(e.getMessage());
        }


    }


    private void robotPublish(RobotModel robotModel, int tradeType, JSONArray json) {
        BigDecimal price, quantity;

        if (robotModel.getRobotType().equals(1)) {
            price = getMarketRobotPrice(robotModel, json);
            quantity = this.getMarketRobotQuantity(robotModel, json, tradeType);
        } else {
            price = this.getRobotPrice(robotModel, tradeType);
            quantity = this.getRobotQuantity(robotModel);
        }
        Assert.isTrue(price.compareTo(BigDecimal.ZERO) > 0, robotModel.getId() + "未获取到价格");
        Assert.isTrue(quantity.compareTo(BigDecimal.ZERO) > 0, robotModel.getId() + "未获取到数量");
        TrusteeModelDto trusteeModel = new TrusteeModelDto();
        trusteeModel.setPrice(price);
        //根据类型 取消买卖单子
        trusteeService.cancelRobot(robotModel.getTradeId(), tradeType, price);
        trusteeModel.setUserId(robotModel.getUserId());
        trusteeModel.setPriority(1);//设置用户交易优先级
        trusteeModel.setTradeId(robotModel.getTradeId());//交易对id
        trusteeModel.setQuantity(quantity);//交易量
        trusteeModel.setTradeType(tradeType);//交易类型 买/卖
        //是否操作钱包开关 开
        if (SwitchEnum.isOn(robotModel.getWalletSwitch())) {
            trusteeModel.setRobot(YesNoEnum.NO.getCode());//机器人
        } else {
            trusteeModel.setRobot(YesNoEnum.YES.getCode());//机器人
        }
        //检查发布
        trusteeService.checkPublish(trusteeModel);
        //发布
        trusteeService.publishTrade(trusteeModel);
        //发布缓存修改
        tradeInfoCacheService.publishCache(trusteeModel.getTradeId(), trusteeModel.getTradeType(), trusteeModel.getPrice(), trusteeModel.getQuantity(), YesNoEnum.YES.getCode().equals(trusteeModel.getRobot()));
    }

    /**
     * TODO 获取机器人发布价格
     * 机器人每次发布买卖时，
     * 发布价格=当前价格+随机值，
     * 随机值=基准价*随机数，随机数区间后台设置，例如-2～+5，基准价后台手动设置，买卖机器人随机数区间分开设置。
     *
     * @param robotModel
     * @param trade
     * @param tradeType
     * @return
     */
    private BigDecimal getRobotPrice(RobotModel robotModel, int tradeType) {
        BigDecimal price = getPrice(robotModel);
        BigDecimal afterPrice = MathUtil.add(price, getRandomPrice(robotModel, tradeType));
        priceLimit(robotModel, afterPrice, price, tradeType);
        return afterPrice;
    }

    /**
     * TODO 随大盘机器人
     *
     * @param robotModel
     * @param trade
     * @param tradeType
     * @return
     */
    private BigDecimal getMarketRobotPrice(RobotModel robotModel, JSONArray json) {
        BigDecimal price;
        // TODO 主流币（其他交易所有参考价格）：买卖读取其他交易所对应币种买一单和卖一单。价格和数量可分别设置乘数发布。
        if (robotModel.getMainstream().equals((byte) 1)) {
            price = json.getBigDecimal(0).multiply(robotModel.getPriceMultiplier());
        } else {
            // TODO 非主流币（无价格参考）：买卖读取交易所BTC交易对买一单和买一单及当前涨跌幅
            //  。价格发布=开盘价（1+BTC涨跌幅+该币设置的涨跌幅偏差） 数量发布=卖一单（或买一单）*数量乘数
            BigDecimal openPrice = tradeService.getOpenPrice(robotModel.getTradeId());
            Assert.isTrue(openPrice.compareTo(BigDecimal.ZERO) >= 0, "获取开盘价失败");
            CurrencyTradeModelDto btcTrade = currencyTradeService.getById(9L);
            TradeInfoDto byCurrencyAndTradeType = tradeService.getByCurrencyAndTradeType(btcTrade);
            BigDecimal add = BigDecimal.ONE.add(byCurrencyAndTradeType.getRise()).add(MathUtil.divPercent(robotModel.getVariationDeviation()));
            price = openPrice.multiply(add);
        }
        return price;
    }

    private BigDecimal getMarketRobotQuantity(RobotModel robotModel, JSONArray json, int tradeType) {
        BigDecimal quantity;
        // TODO 主流币（其他交易所有参考价格）：买卖读取其他交易所对应币种买一单和卖一单。价格和数量可分别设置乘数发布。
        if (robotModel.getMainstream().equals((byte) 1)) {
            quantity = json.getBigDecimal(1).multiply(robotModel.getQuantityMultiplier());
        } else {
            // TODO 非主流币（无价格参考）：买卖读取交易所BTC交易对买一单和买一单及当前涨跌幅
            //  。价格发布=开盘价（1+BTC涨跌幅+该币设置的涨跌幅偏差） 数量发布=卖一单（或买一单）*数量乘数
            quantity = findFirstOrderNum(tradeType).multiply(robotModel.getQuantityMultiplier());
        }
        return quantity;
    }

    private BigDecimal findFirstOrderNum(int tradeType) {
        if (BuySellEnum.BUY.getCode().equals(tradeType)) {
            return trusteeService.findFirstBuy(9L).getQuantity();
        } else {
            return trusteeService.findFirstSell(9L).getQuantity();
        }
    }

    /**
     * 买卖涨跌幅4个参数都可以分别设置，
     * 当发布价格超出所设定的涨跌幅时，不发布订单，
     * 涨跌幅度不填时（即为0时），不受涨跌幅控制。
     */
    private void priceLimit(RobotModel robotModel, BigDecimal afterPrice, BigDecimal price, int tradeType) {
        BigDecimal sub = MathUtil.sub(afterPrice, tradeService.getOpenPrice(robotModel.getTradeId()));
        if (BuySellEnum.SELL.getCode().equals(tradeType)) {
            checkPrice(sub.compareTo(BigDecimal.ZERO) > -1 ? robotModel.getSellRise() : robotModel.getSellFall(), robotModel.getTradeId(), sub);
        } else {
            checkPrice(sub.compareTo(BigDecimal.ZERO) > -1 ? robotModel.getBuyRise() : robotModel.getBuyFall(), robotModel.getTradeId(), sub);
        }
    }

    /**
     * 检查涨跌幅限制
     *
     * @param num
     * @param price
     * @param ratio
     */
    private void checkPrice(int num, long tradeId, BigDecimal sub) {
        BigDecimal openPrice = tradeService.getOpenPrice(tradeId);
        Assert.isTrue(openPrice != null && openPrice.compareTo(BigDecimal.ZERO) > 0, "未获取到开盘价" + tradeId);
        if (num != 0 && MathUtil.div(BigDecimal.valueOf(num), BigDecimal.valueOf(100)).compareTo(MathUtil.div(sub.abs(), openPrice)) < 0) {
            throw new BusinessException("超出涨跌幅限制");
        }
    }

    /**
     * TODO 获取随机区间的价格
     * 随机值=基准价*随机数，随机数区间后台设置，例如-2～+5，基准价后台手动设置，买卖机器人随机数区间分开设置。
     *
     * @return
     */
    private BigDecimal getRandomPrice(RobotModel robotModel, int tradeType) {
        if (BuySellEnum.SELL.getCode().equals(tradeType)) {
            BigDecimal v = BigDecimal.valueOf(Math.random()).multiply(robotModel.getMaxSellPrice().subtract(robotModel.getMinSellPrice()));
            return MathUtil.mul(v.add(robotModel.getMinSellPrice()), robotModel.getBasePrice());
        } else {
            BigDecimal v = BigDecimal.valueOf(Math.random()).multiply((robotModel.getMaxBuyPrice().subtract(robotModel.getMinBuyPrice())));
            return MathUtil.mul(v.add(robotModel.getMinBuyPrice()), robotModel.getBasePrice());
        }
    }


    private BigDecimal getPrice(RobotModel robotModel) {
        return tradeService.getPriceByTradeId(robotModel.getTradeId());
    }

    /**
     * TODO 获取机器人发布数量
     * 发布订单时数量在当前选定区间内随机获取。设置五个数量区间（最小量和最大量），并配置开关是否启用，
     * 每间隔 X（数字可调）分钟，在已开启的区间内任意选一区间做为当前发布订单数量区间。
     *
     * @param robotModel
     * @return
     */
    private BigDecimal getRobotQuantity(RobotModel robotModel) {
        Integer interval = valueRepository.get(KeyConstant.KEY_ROOT_QUANTITY_INTERVAL);
        List<RobotModel.Quantity> quantityInterval = robotModel.getQuantityInterval().stream().filter(e -> e.getSectionState() == 1).collect(Collectors.toList());
        if (quantityInterval.size() <= 0) {
            throw new BusinessException("数量区间未设置");
        }
        if (interval == null || quantityInterval.size() <= interval) {
            interval = RandomUtils.nextInt(0, quantityInterval.size());
            valueRepository.setKey(KeyConstant.KEY_ROOT_QUANTITY_INTERVAL, interval, robotModel.getChangeIntervalTime(), TimeUnit.MINUTES);
        }
        RobotModel.Quantity quantity = quantityInterval.get(interval);
        BigDecimal add = MathUtil.add(MathUtil.mul(BigDecimal.valueOf(Math.random()), MathUtil.sub(quantity.getMaxQuantity(), quantity.getMinQuantity())), quantity.getMinQuantity());
        return add.setScale(6, BigDecimal.ROUND_DOWN);
    }
}
