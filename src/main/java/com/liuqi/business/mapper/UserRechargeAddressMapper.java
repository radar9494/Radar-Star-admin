package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.UserRechargeAddressModel;
import com.liuqi.business.model.UserRechargeAddressModelDto;
import org.apache.ibatis.annotations.Param;


public interface UserRechargeAddressMapper extends BaseMapper<UserRechargeAddressModel, UserRechargeAddressModelDto>{

    UserRechargeAddressModelDto getRechargeAddress(@Param("userId") Long userId, @Param("protocol") Integer protocol);


    Long findBindingUserIdByAddress(@Param("address")String address, @Param("protocol") Integer protocol);
}
