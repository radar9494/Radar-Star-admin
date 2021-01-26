package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.HelpTypeModel;
import com.liuqi.business.model.HelpTypeModelDto;

import java.util.List;

public interface HelpTypeService extends BaseService<HelpTypeModel,HelpTypeModelDto> {

    String  getNameById(Long id, boolean hasSelf);

    List<HelpTypeModelDto> getUsingFirstLevel();

    List<HelpTypeModelDto> getFirstLevel();

    List<HelpTypeModelDto> getUsingSub(Long parentId);
    List<HelpTypeModelDto> getSub(Long parentId);


}
