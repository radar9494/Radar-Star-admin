package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.OtcWalletModel;
import com.liuqi.business.model.OtcWalletModelDto;
import com.liuqi.business.model.WalletStaticModel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;


public interface OtcWalletMapper extends BaseMapper<OtcWalletModel,OtcWalletModelDto>{


    @Select("select  *  from t_otc_wallet where user_id=#{userId} and currency_id=#{currencyId}")
    OtcWalletModelDto getByUserAndCurrencyId(@Param("userId") Long userId, @Param("currencyId")Long currencyId);

    int modifyWallet(@Param("userId") Long userId,@Param("currencyId") Long currencyId,
                         @Param("changeUsing") BigDecimal changeUsing, @Param("changeFreeze") BigDecimal changeFreeze);
    @Select("SELECT\tsum( `USING` ) AS `USING`,\tsum( freeze ) AS freeze ,\tcurrency_id as currencyId FROM  \tt_otc_wallet   GROUP BY\tcurrency_id  ")
    List<WalletStaticModel> groupByCurrencyId();


    OtcWalletModel getTotal(@Param("currencyId") Long id,@Param("status")Integer status);
}
