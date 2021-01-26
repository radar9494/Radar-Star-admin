package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.SlideModel;
import com.liuqi.business.model.SlideModelDto;

import java.util.List;

public interface SlideService extends BaseService<SlideModel, SlideModelDto> {


    /**
     * 创建
     * @param slideModel
     */
    void slideAdd(SlideModel slideModel);


    /**
     * 删除
     * @param id
     * @return
     */
    boolean slideDelete(Long id);

    List<SlideModelDto> getCanShow(Integer type);
    /**
     * 查询轮播图
     * @return
     */
    List<SlideModelDto> findSlide(Integer type);
}
