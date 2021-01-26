package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.SuperNodeChargeModel;
import com.liuqi.business.model.SuperNodeChargeModelDto;

import java.util.Date;


public interface SuperNodeChargeMapper extends BaseMapper<SuperNodeChargeModel,SuperNodeChargeModelDto>{


    SuperNodeChargeModel getByDate(Date date);
}
