package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.HelpTypeModel;
import com.liuqi.business.model.HelpTypeModelDto;

import java.util.List;


public interface HelpTypeMapper extends BaseMapper<HelpTypeModel,HelpTypeModelDto> {


    HelpTypeModel getByName(String name);

    List<HelpTypeModelDto> getByParent(Long parentId);
}
