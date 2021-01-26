package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.InformationModel;
import com.liuqi.business.model.InformationModelDto;


public interface InformationMapper extends BaseMapper<InformationModel,InformationModelDto>{


    InformationModel getByInfoId(Long infoId);

    int getTotal();
}
