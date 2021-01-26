package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.TimestampLevelModel;
import com.liuqi.business.model.TimestampLevelModelDto;
import com.liuqi.business.model.UserModelDto;

public interface TimestampLevelService extends BaseService<TimestampLevelModel,TimestampLevelModelDto>{


    void initSubLevel(UserModelDto item, Long userId);

    TimestampLevelModel getByUserId(Long id);

    void initParentLevel(UserModelDto item, Long userId);
}
