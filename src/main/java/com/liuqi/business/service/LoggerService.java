package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.LoggerModel;
import com.liuqi.business.model.LoggerModelDto;

public interface LoggerService extends BaseService<LoggerModel, LoggerModelDto> {

    /**
     * 新增
     * @param type  LoggerModel
     * @param content
     * @param title
     * @param operId
     */
    void insert(int type, String content, String title, Long operId);
}
