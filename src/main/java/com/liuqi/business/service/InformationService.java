package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.InformationModel;
import com.liuqi.business.model.InformationModelDto;

import java.util.List;

public interface InformationService extends BaseService<InformationModel,InformationModelDto>{

    /**
     * 根据资讯id获取
     * @param infoId
     * @return
     */
    InformationModel getByInfoId(Long infoId);

    /**
     * 获取总数量
     * @return
     */
    int getTotal();
    /**
     * 获取资讯
     */
    void thridInfo();

}
