package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.EmailModel;
import com.liuqi.business.model.EmailModelDto;

import java.util.List;

public interface EmailService extends BaseService<EmailModel,EmailModelDto>{

    List<EmailModelDto> getUsingList();

    EmailModelDto getCanUsing();

}
