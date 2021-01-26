package com.liuqi.business.mapper;

import com.liuqi.business.dto.WalletDto;
import com.liuqi.business.dto.stat.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StatMapper {

    @Select("select DATE(t.create_time) as date,count(*) as count from t_user t where t.create_time>=#{startDate} and t.create_time<=#{endDate} group by DATE(t.create_time) ")
    List<UserStatDto> userStat(@Param("startDate") Date startDate,@Param("endDate") Date endDate);

    @Select("select DATE(t.create_time) as date,t.currency_id as currencyId,sum(quantity) as total from t_recharge t where t.create_time>=#{startDate} and t.create_time<=#{endDate}  group by DATE(t.create_time),t.currency_id ")
    List<RechargeExtractStatDto> rechargeStat(@Param("startDate") Date startDate,@Param("endDate") Date endDate);

    @Select("select DATE(t.create_time) as date,t.currency_id as currencyId,sum(quantity) as total from t_extract t  where t.create_time>=#{startDate} and t.create_time<=#{endDate} and `status`=1 group by DATE(t.create_time),currency_id" )
    List<RechargeExtractStatDto> extractStat(@Param("startDate") Date startDate,@Param("endDate") Date endDate);

    @Select("select DATE(t.create_time) as date,sum(t.trade_quantity) as quantity,t.trade_id as tradeId  from t_trade_record t  where t.create_time>=#{startDate} and t.create_time<=#{endDate} and t.`robot`=2 group by DATE(t.create_time),t.trade_id ")
    List<TradeStatDto> tradeStat(@Param("startDate") Date startDate,@Param("endDate") Date endDate);

    /**
     * 待提现数量
     * @return
     */
    @Select("select t.currency_id as currencyId,sum(t.quantity) as total,count(*) as quantity from t_extract t where t.`status`=0  group by t.currency_id ")
    List<WaitExtractStatDto> waitExtractStat();

    /**
     * 待提现数量
     * @param currencyId
     * @return
     */
    @Select("select t.currency_id as currencyId,sum(t.quantity) as total,count(*) as quantity from t_extract t where t.`status`=0  and  t.currency_id=#{currencyId} ")
    List<WaitExtractStatDto> waitExtractStatByCurrencyId(@Param("currencyId")Long currencyId);

    /**
     * 异常订单数量统计
     * @return
     */
    @Select("select trade_id as tradeId,count(*) as count from t_trustee t where t.`status`=3 group by t.trade_id ")
    List<TradeErrorDto> findError();
    /**
     * 买未返还异常数量统计
     * @return
     */
    @Select("select t.trade_id as tradeId,count(*) as count from t_trade_record t where t.buy_wallet_status =2 group by t.trade_id")
    List<TradeErrorDto> findBuyTradeBackError();

    /**
     * 卖未返还异常数量统计
     * @return
     */
    @Select("select t.trade_id as tradeId,count(*) as count from t_trade_record t where t.sell_wallet_status =2 group by t.trade_id")
    List<TradeErrorDto> findSellTradeBackError();


    @Select("<script>" +
            "select store_id as storeId,currency_id as currencyId,trade_type as tradeType,status,sum(money) as money " +
            "from t_ctc_order " +
            "<trim prefix='where' prefixOverrides='AND|OR'> "+
            "<if test='startDate!=null'>"+
            " and create_time>=#{startDate} " +
            "</if> "+
            "<if test='endDate!=null'>"+
            "<![CDATA[and t.create_time<=#{endDate}]]>"+
            "</if> "+
            "</trim> "+
            "group by store_id,currency_id,status,trade_type"+
            "</script>")
    List<CtcStatDto> ctcStat(@Param("startDate") Date startDate,@Param("endDate") Date endDate);

    @Select("select currency_id as currencyId,sum(`using`) as `using`,sum(freeze) as freeze from t_user_wallet  group by currency_id")
    List<WalletDto> getUsing();
    @Select("select currency_id as currencyId,sum(`locking`) as `using`,sum(freeze) as freeze from t_lock_wallet  group by currency_id")
    List<WalletDto> getLock();
    @Select("select  sum(quantity)   from t_recharge  where currency_id=#{currencyId} and status=1")
    BigDecimal rechargeTotal(Long currencyId);

    @Select("select  sum(quantity)   from t_extract  where currency_id=#{currencyId} and status=1")
    BigDecimal extractgeTotal(Long currencyId);
    @Select("select sum(trade_quantity) from t_trade_record where trade_type=1  and trade_id=#{id} and robot=0")
    BigDecimal getSellTotal(Long id);
    @Select("select sum(trade_quantity) from t_trade_record where trade_type=0 and trade_id=#{id} and robot=0")
    BigDecimal getBuyTotal(Long id);
}
