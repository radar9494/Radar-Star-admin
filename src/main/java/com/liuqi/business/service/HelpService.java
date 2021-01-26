package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.HelpModel;
import com.liuqi.business.model.HelpModelDto;

import java.util.List;

public interface HelpService extends BaseService<HelpModel,HelpModelDto> {

    List<HelpModelDto> getByLikeTitle(String title);

    List<HelpModelDto> getByTypeId(Long typeId);

}
