package com.liuqi.business.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.liuqi.business.model.UserApiKeyModel;
import com.liuqi.business.model.UserApiKeyModelDto;
import com.liuqi.business.service.UserApiKeyHandleService;
import com.liuqi.business.service.UserApiKeyService;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * description: UserApiKeyHandleServiceImpl <br>
 * date: 2020/6/8 9:45 <br>
 * author: chenX <br>
 * version: 1.0 <br>
 */
@Service
public class UserApiKeyHandleServiceImpl implements UserApiKeyHandleService {


    private UserApiKeyService userApiKeyService;

    @Autowired
    public UserApiKeyHandleServiceImpl(UserApiKeyService userApiKeyService) {
        this.userApiKeyService = userApiKeyService;
    }




}
