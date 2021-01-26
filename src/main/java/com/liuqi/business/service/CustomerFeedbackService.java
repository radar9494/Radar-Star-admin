package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.CustomerFeedbackModel;
import com.liuqi.business.model.CustomerFeedbackModelDto;

public interface CustomerFeedbackService extends BaseService<CustomerFeedbackModel,CustomerFeedbackModelDto>{


    void feedback(CustomerFeedbackModel model);

    void deal(Long id);
}
