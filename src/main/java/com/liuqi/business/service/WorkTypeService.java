package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.WorkTypeModel;
import com.liuqi.business.model.WorkTypeModelDto;

import java.util.List;

public interface WorkTypeService extends BaseService<WorkTypeModel,WorkTypeModelDto> {

    String getNameById(Long typeId);

    List<WorkTypeModelDto> getAll();

    List<WorkTypeModelDto> getUsing();


}
