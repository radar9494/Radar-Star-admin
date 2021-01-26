package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.dto.UserTotalDto;
import com.liuqi.business.model.OtcOrderRecordModel;
import com.liuqi.business.model.OtcOrderRecordModelDto;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface OtcOrderRecordMapper extends BaseMapper<OtcOrderRecordModel,OtcOrderRecordModelDto>{


    int getMyBuyNoPay(@Param("userId") Long userId,@Param("status")Integer status);

    int canCancel(@Param("orderId") Long orderId, @Param("statusList") List<Integer> statusList);

    List<UserTotalDto> statBuy(@Param("userIdList") List<Long> userIdList, @Param("statusList") List<Integer> statusList, @Param("currencyId") Long currencyId);
    List<UserTotalDto> statSell(@Param("userIdList") List<Long> userIdList,@Param("statusList") List<Integer> statusList, @Param("currencyId") Long currencyId);

    BigDecimal getSuccessQuantity(@Param("orderId") Long orderId,@Param("status")Integer status);


    OtcOrderRecordModel getMyWaitPay(Long userId);
}
