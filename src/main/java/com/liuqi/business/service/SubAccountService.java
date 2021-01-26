package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.SubAccountModel;
import com.liuqi.business.model.SubAccountModelDto;

import javax.servlet.http.HttpServletRequest;

public interface SubAccountService extends BaseService<SubAccountModel,SubAccountModelDto>{


    void add(String name, String pwd, Long userId, int time, HttpServletRequest request);

    void delete(Long id);

    int getByUserIdSubId(Long userId, Long id);

    String changAccount(Long id,Long userId,String pwd);

    void insertModel(Long userId,Long subId,String token);
}
