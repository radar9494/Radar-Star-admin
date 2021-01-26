package com.liuqi.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class IPLimit {

    public static final int LIMIT_COUNT=1;//限制次数
    public static final Long LIMIT_TIME=2L;//限制时间
    public static final TimeUnit LIMIT_TIMEUNIT=TimeUnit.SECONDS;//限制时间单位
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * ip是否能访问
     * @param method
     * @param ip
     * @return
     */
    public boolean canContinue(String method,String ip){
        boolean canContinue=false;
        String key="limit:"+method+":"+ip;
        String countStr=stringRedisTemplate.opsForValue().get(key);
        if(StringUtils.isEmpty(countStr)) {
            stringRedisTemplate.opsForValue().set(key, "1", LIMIT_TIME, LIMIT_TIMEUNIT);
            canContinue=true;
        }else{
            int count=Integer.valueOf(countStr);
            if(count<LIMIT_COUNT){
                stringRedisTemplate.opsForValue().increment(key,1);
                canContinue=true;
            }
        }
        return canContinue;
    }
}
