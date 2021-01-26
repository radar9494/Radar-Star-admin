package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.ContentModel;
import com.liuqi.business.model.ContentModelDto;

import java.util.List;

public interface ContentService extends BaseService<ContentModel, ContentModelDto> {

    /**
     * 获取最新的缓存信息
     * @param pageSize
     * @return
     */
    List<ContentModelDto> getNewContent(Integer pageSize);
}
