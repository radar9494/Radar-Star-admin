package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.MiningWalletModel;
import com.liuqi.business.model.MiningWalletModelDto;
import com.liuqi.business.model.WalletStaticModel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;


public interface MiningWalletMapper extends BaseMapper<MiningWalletModel,MiningWalletModelDto>{

    @Update(" update  t_mining_wallet\n" +
            " set\n" +
            " `using` = `using`+#{changeUsing},\n" +
            " `freeze` = `freeze`+#{freeze}\n" +
            " where\n" +
            " `user_id` = #{userId}\n" +
            " AND `currency_id` = #{currencyId}\n" +
            " AND `using`>=0-#{changeUsing}")
    int updateUsing(long userId,long currencyId,BigDecimal changeUsing,BigDecimal freeze);

    @Select("select * from t_mining_wallet where user_id = #{userId} and currency_id = #{currencyId}")
    MiningWalletModel findByUserIdAndCurrencyId(long userId,long currencyId);

    @Select("SELECT\tsum( `USING` ) AS `USING` ,\tcurrency_id as currencyId  FROM  \tt_mining_wallet   GROUP BY\tcurrency_id  ")
    List<WalletStaticModel> groupByCurrencyId();

    @Select("select * from t_mining_wallet where freeze>0")
    List<MiningWalletModel> getFreeze();


    MiningWalletModel getTotal(@Param("currencyId") Long id,@Param("status") Integer status);
}
