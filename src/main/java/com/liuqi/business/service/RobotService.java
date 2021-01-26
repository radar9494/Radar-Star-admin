package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.RobotModel;
import com.liuqi.business.model.RobotModelDto;

public interface RobotService extends BaseService<RobotModel,RobotModelDto> {


    void removeById(Long id);

    void pause(Long id);

    void resume(Long id);

}
