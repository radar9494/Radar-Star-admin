package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.UserApiKeyModel;
import com.liuqi.business.model.UserApiKeyModelDto;
import org.apache.ibatis.annotations.Delete;


public interface UserApiKeyMapper extends BaseMapper<UserApiKeyModel,UserApiKeyModelDto>{


    UserApiKeyModelDto getByApiKey(String apiKey);

    UserApiKeyModelDto getByUserId(Long userId);

}
