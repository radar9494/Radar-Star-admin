package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.PledgeWalletModel;
import com.liuqi.business.model.PledgeWalletModelDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;


public interface PledgeWalletMapper extends BaseMapper<PledgeWalletModel,PledgeWalletModelDto>{

    @Select("select * from t_pledge_wallet where user_id=#{userId}")

    PledgeWalletModelDto getByUserId(Long userId);

    int modifyWalletUsing(@Param("userId") Long userId,@Param("changeUsing") BigDecimal quantity);

    PledgeWalletModel getTotal(@Param("status") Integer status);
}
