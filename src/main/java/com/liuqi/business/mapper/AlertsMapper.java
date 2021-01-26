package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.AlertsModel;
import com.liuqi.business.model.AlertsModelDto;


public interface AlertsMapper extends BaseMapper<AlertsModel, AlertsModelDto> {

    Integer queryPagesByTime();
}
