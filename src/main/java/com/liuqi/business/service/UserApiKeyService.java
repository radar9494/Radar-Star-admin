package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.UserApiKeyModel;
import com.liuqi.business.model.UserApiKeyModelDto;
import com.liuqi.response.ReturnResponse;

public interface UserApiKeyService extends BaseService<UserApiKeyModel,UserApiKeyModelDto>{

    /**
     * 创建api
     * @param userId
     * @return
     */
    ReturnResponse createApi(Long userId,Integer status);

    /**
     * 启用
     * @param userId
     * @return
     */
    ReturnResponse startApi(Long userId,String code);

    /**
     * 停用
     * @param userId
     * @return
     */
    ReturnResponse stopApi(Long userId);

    /**
     * 更换私钥
     * @param userId
     * @return
     */
    ReturnResponse changeSecretKey(Long userId,String code);

    UserApiKeyModelDto getByUserId(Long userId);

    UserApiKeyModelDto getByApiKey(String apiKey);


    ReturnResponse getHiddenApi(long userId);

    ReturnResponse getApi(long userId, String code);
}
