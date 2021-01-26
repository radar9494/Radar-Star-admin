package com.liuqi.base;

import com.liuqi.redis.NumRepository;
import com.liuqi.redis.RedisRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class RedisTest extends BaseTest {

    @Autowired
    private RedisRepository redisRepository ;
    @Autowired
    private NumRepository numRepository;

    @Test
    public void test01(){
        String key="tt";
        String s=redisRepository.getString(key);
        if(StringUtils.isEmpty(s)) {
            redisRepository.set(key, "1", 1L, TimeUnit.DAYS);
        }
        System.out.println(s);
        redisRepository.incrOne(key);
        System.out.println(redisRepository.getString(key));
        redisRepository.incrOne(key);
        System.out.println(redisRepository.getString(key));
        redisRepository.incrOne(key);
        System.out.println(redisRepository.getString(key));
    }

    @Test
    public void teso02(){
        CountDownLatch countDownLatch=new CountDownLatch(20);
        for(int i=0;i<20;i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    countDownLatch.countDown();
                    try {
                        countDownLatch.await();
                        System.out.println("ctc->" + numRepository.getCtcTradeCode());
                        System.out.println("otc->" + numRepository.getOtcTradeCode());
                        System.out.println("work->" + numRepository.getWorkCode());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
