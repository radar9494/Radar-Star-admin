package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.OtcOrderModel;
import com.liuqi.business.model.OtcOrderModelDto;
import org.apache.ibatis.annotations.Param;


public interface OtcOrderMapper extends BaseMapper<OtcOrderModel,OtcOrderModelDto>{


    void updateCancelStatus(@Param("orderId") Long orderId, @Param("cancel")Integer cancel);
}
