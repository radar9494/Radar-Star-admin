package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.AlertsModel;
import com.liuqi.business.model.AlertsModelDto;

public interface AlertsService extends BaseService<AlertsModel, AlertsModelDto> {


    Integer queryPagesByTime();

}
