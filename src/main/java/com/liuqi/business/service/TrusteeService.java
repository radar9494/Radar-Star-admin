package com.liuqi.business.service;

import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.TrusteeModel;
import com.liuqi.business.model.TrusteeModelDto;

import java.math.BigDecimal;
import java.util.List;

public interface TrusteeService extends BaseService<TrusteeModel,TrusteeModelDto>{
    /**
     * 交易验证
     *
     * @param trusteeModelDto
     */
    void checkPublish(TrusteeModelDto trusteeModelDto);

    /**
     * 发布交易
     *
     * @param trusteeOrder
     */
    void publishTrade(TrusteeModelDto trusteeOrder);

    /**
     * 用户未成功的订单
     *
     * @param userId
     * @param tradeId
     * @param limit
     * @param count 查询数量
     * @return
     */
    List<TrusteeModelDto> findUserNoSuccess(Long userId, Long tradeId,boolean limit,Integer count);


    /**
     * 取消交易
     * @param trusteeId  订单id
     * @param userId    当前用户id
     * @param checkUser  是否检查用户发布
     */
    void cancel(Long trusteeId,Long userId,boolean checkUser);
    /**
     * 查询卖出最低价格
     * @param tradeId 交易对id
     * @return
     */
    BigDecimal findTrusteeSellMinPrice(Long tradeId);
    /**
     * 查询买入最高价格
     * @param tradeId 交易对id
     * @return
     */
    BigDecimal findTrusteeBuyMaxPrice(Long tradeId);
    /**
     * 取消机器人单子
     * @param tradeId
     * @param curPrice
     */
    void cancelRobot(Long tradeId,Integer tradeType,BigDecimal curPrice);
    /**
     * 查询买1
     * @param tradeId 交易对id
     * @return
     */
    TrusteeModelDto findFirstBuy(Long tradeId);
    /**
     * 查询买1
     * @param tradeId 交易对id
     * @return
     */
    TrusteeModelDto findFirstSell(Long tradeId);
    /**
     * 查询托管订单信息
     * @param tradeType
     * @param tradeId
     * @return
     */
    List<TrusteeModelDto> findTrusteeOrderList(Integer tradeType, Long tradeId,int num);

    /**
     * 取消所有机器人交易
     * @param tradeId
     * @param tradeType
     */
    void cancelAllRobot(Long tradeId, Integer tradeType);

    /**
     * 删除机器人取消的订单
     */
    void deleteRobotCancel();

    /**
     * 修改异常单
     */
    void errorModify();
}
