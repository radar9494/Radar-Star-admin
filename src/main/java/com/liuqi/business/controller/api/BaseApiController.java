package com.liuqi.business.controller.api;

import com.liuqi.base.BaseController;
import com.liuqi.business.enums.UsingEnum;
import com.liuqi.business.enums.api.ApiResultEnum;
import com.liuqi.business.model.UserApiKeyModelDto;
import com.liuqi.business.service.UserApiKeyService;
import com.liuqi.response.APIResult;
import com.liuqi.utils.APISignUtils;
import com.liuqi.utils.SignUtil;
import org.apache.commons.lang3.StringUtils;
import org.mockito.cglib.beans.BeanMap;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tanyan
 * @create 2020-07=20
 * @description
 */
public class BaseApiController extends BaseController {

    @Autowired
    private UserApiKeyService userApiKeyService;


    protected APIResult<Long> checkUser(HttpServletRequest request, Map<String,Object> params){
        String apiKey=request.getHeader("apiKey");
        if(StringUtils.isEmpty(apiKey)){
            return APIResult.fail(ApiResultEnum.ERROR_HEAD_API,null);
        }
        UserApiKeyModelDto userApi=userApiKeyService.getByApiKey(apiKey);
        if(userApi==null){
            return APIResult.fail(ApiResultEnum.NO_APPLY,null);
        }
        if(UsingEnum.NOTUSING.getCode().equals(userApi.getStatus())){
            return APIResult.fail(ApiResultEnum.ERROR_API_NO_USING,null);
        }
        String secretKey= SignUtil.getDecode(userApi.getSecretKey());
        String timestampStr=request.getHeader("timestamp");
        if(StringUtils.isEmpty(timestampStr)){
            return APIResult.fail(ApiResultEnum.ERROR_HEAD_TIME,null);
        }
        String sign=request.getHeader("sign");
        if(StringUtils.isEmpty(sign)){
            return APIResult.fail(ApiResultEnum.ERROR_HEAD_SIGN,null);
        }
        //时间判断
        Long timestamp=Long.valueOf(timestampStr);
        Long curTime=System.currentTimeMillis();
        if(Math.abs(timestamp-curTime)>60*1000){
            return APIResult.fail(ApiResultEnum.ERROR_TIME,null);
        }
        String curSign= APISignUtils.signData(timestampStr,params,secretKey);
        System.out.println("-----curSign->"+curSign);
        System.out.println("-----sign->"+sign);
        return curSign.equalsIgnoreCase(sign)?APIResult.success(userApi.getUserId()):APIResult.fail(ApiResultEnum.ERROR_PARAMS,-1L);
    }


    /**
     * 将对象装换为map
     *
     * @param bean
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = new HashMap<>();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 将map装换为javabean对象
     *
     * @param map
     * @param bean
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

}
