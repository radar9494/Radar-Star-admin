package com.liuqi.business.dto.api.response;

import com.liuqi.business.model.UserWalletModel;
import com.liuqi.business.model.UserWalletModelDto;
import com.liuqi.utils.MathUtil;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tanyan
 * @create 2020-07=20
 * @description
 */
@Data
@Builder
public class WalletRespDto {
    private String  currency;
    private String using;
    private String freeze;



    public static WalletRespDto transfer(String currency,UserWalletModelDto userWallet){
        return  WalletRespDto.builder()
                .currency(currency)
                .using(MathUtil.format(userWallet.getUsing()))
                .freeze(MathUtil.format(userWallet.getFreeze())).build();
    }

    public static List<WalletRespDto> transferList(List<UserWalletModelDto> userWalletList){
        List<WalletRespDto> list=new ArrayList<>();
        for(UserWalletModelDto wallet:userWalletList){
            list.add(WalletRespDto.transfer(wallet.getCurrencyName(),wallet));
        }
        return list;
    }
}
