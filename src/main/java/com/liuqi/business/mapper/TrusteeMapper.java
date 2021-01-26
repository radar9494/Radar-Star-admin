package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.TrusteeModel;
import com.liuqi.business.model.TrusteeModelDto;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;


public interface TrusteeMapper extends BaseMapper<TrusteeModel,TrusteeModelDto>{

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
     * 查询待取消的机器人单子
     * @param tradeId
     * @param tradeType
     * @param curPrice
     * @return
     */
    List<Long> queryRobotPrice(@Param("tradeId")Long tradeId,@Param("tradeType") Integer tradeType,@Param("curPrice")BigDecimal curPrice);
    /**
     * 查询托管订单信息
     * @param tradeType
     * @param tradeId
     * @return
     */
    List<TrusteeModelDto> findTrusteeOrderList(@Param("tradeType") Integer tradeType, @Param("tradeId") Long tradeId,@Param("num") int num);


    List<Long> queryRobot(@Param("tradeId")Long tradeId,@Param("tradeType") Integer tradeType);

    /**
     * 卖1
     * @param tradeId
     * @return
     */
    TrusteeModelDto findFirstSell(@Param("tradeId") Long tradeId);

    /**
     * 买1
     * @param tradeId
     * @return
     */
    TrusteeModelDto findFirstBuy(@Param("tradeId") Long tradeId);

    @Delete("delete from t_trustee where status=#{status} and robot=#{robot} and trade_quantity=0")
    void deleteRobotCancel(@Param("status")Integer status,@Param("robot")Integer robot);

    @Update("update t_trustee set status=#{status}  where status=#{modifyStatus} ")
    void updateStatus(@Param("modifyStatus") Integer modifyStatus, @Param("status") Integer status);
}
