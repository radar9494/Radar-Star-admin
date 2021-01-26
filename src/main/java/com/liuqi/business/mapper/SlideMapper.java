package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.SlideModel;
import com.liuqi.business.model.SlideModelDto;


public interface SlideMapper extends BaseMapper<SlideModel,SlideModelDto>{


    void removeById(Long id);
}
