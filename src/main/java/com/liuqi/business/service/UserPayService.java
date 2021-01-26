package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.UserPayModel;
import com.liuqi.business.model.UserPayModelDto;

import java.util.List;

public interface UserPayService extends BaseService<UserPayModel,UserPayModelDto>{

    /**
     * 用户收款信息
     * @param userId
     * @return
     */
    List<UserPayModelDto> getByUserId(Long userId);

    /**
     * 获取用户付款信息
     * @param userId
     * @param payType
     * @return
     */
    UserPayModelDto getByUserId(Long userId,Integer payType);

    /**
     * 初始化
     * @param userId
     * @param payType
     */
    void init(Long userId, Integer payType);
}
