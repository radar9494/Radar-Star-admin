package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.WorkDetailModel;
import com.liuqi.business.model.WorkDetailModelDto;

import java.util.List;

public interface WorkDetailService extends BaseService<WorkDetailModel,WorkDetailModelDto> {

    /**
     * 保存明细
     * @param workId
     * @param content
     * @param file1
     * @param file2
     * @param type
     */
    void saveDetail(Long workId, String content, String file1, String file2, String file3, Integer type);

    /**
     * 获取工单明细
     * @param id
     * @return
     */
    List<WorkDetailModelDto> getByWork(Long id);
}
