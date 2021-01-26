package com.liuqi.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MethodLimit {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 剩余能执行次数
     * @param method  方法
     * @param parmas  参数
     * @param limitCount 最大次数
     * @param limitTime  限制时间
     * @param timeUnit   限制单位
     * @return
     */
    public int residueTimes(String method,String parmas,int limitCount,Long limitTime,TimeUnit timeUnit){
        int residueTimes=limitCount;
        String key="limit:"+method+":"+parmas;
        String countStr=stringRedisTemplate.opsForValue().get(key);
        if(StringUtils.isEmpty(countStr)) {
            stringRedisTemplate.opsForValue().set(key, "1", limitTime, timeUnit);
            //剩余见一次
        }else{
            int count=Integer.valueOf(countStr);
            if(count<limitCount){
                stringRedisTemplate.opsForValue().increment(key,1);
            }
            //剩余次数
            residueTimes=limitCount-count;
        }
        return residueTimes;
    }

    public void clean(String method,String parmas){
        String key="limit:"+method+":"+parmas;
        stringRedisTemplate.delete(key);
    }
}
