package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.WorkModel;
import com.liuqi.business.model.WorkModelDto;

public interface WorkService extends BaseService<WorkModel,WorkModelDto> {

    /**
     * 发布
     * @param userId
     * @param typeId
     * @param title
     * @param phone
     * @param email
     * @param content
     * @param file1
     * @param file2
     */
    void publish(Long userId, Long typeId, String title, String phone, String email, String content, String file1, String file2, String file3);

    /**
     * 回复
     * @param workId
     * @param content
     * @param file1Path
     * @param file2Path
     */
    void reply(Long workId, String content, String file1Path, String file2Path, String file3Path, Integer type);

    /**
     * 完结
     * @param workId
     */
    void userEnd(Long workId, Integer result);
    /**
     * 完结
     * @param workId
     */
    void sysEnd(Long workId, Integer result);
}
