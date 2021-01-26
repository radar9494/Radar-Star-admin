package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.dto.api.AllWalletDto;
import com.liuqi.business.model.UserWalletModel;
import com.liuqi.business.model.UserWalletModelDto;
import com.liuqi.business.model.WalletStaticModel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;


public interface UserWalletMapper extends BaseMapper<UserWalletModel,UserWalletModelDto>{


    UserWalletModelDto getByUserAndCurrencyId(@Param("userId") Long userId, @Param("currencyId") Long currencyId);

    int modifyWallet(@Param("userId") Long userId, @Param("currencyId") Long currencyId, @Param("changeUsing") BigDecimal changeUsing, @Param("changeFreeze") BigDecimal changeFreeze);

    int modifyWalletUsing(@Param("userId") Long userId, @Param("currencyId") Long currencyId, @Param("changeUsing") BigDecimal changeUsing);

    int modifyWalletFreeze(@Param("userId") Long userId, @Param("currencyId") Long currencyId,@Param("changeFreeze") BigDecimal changeFreeze);

   @Select("SELECT\tsum( `USING` ) AS `USING`,\tsum( freeze ) AS freeze ,\tcurrency_id as currencyId  FROM  \tt_user_wallet   GROUP BY\tcurrency_id  ")
    List<WalletStaticModel> groupByCurrencyId();

     UserWalletModel getTotal(@Param("currencyId") Long currencyId,@Param("status")Integer status);
    @Select("select w.`using`,w.`freeze`,c.name as currencyName ,u.name as userName from t_user_wallet w join t_user u on w.user_id = u.id join t_currency c on w.currency_id =c.id where w.currency_id=#{currencyId}")
    List<AllWalletDto> allWallet(Long currencyId);
}
