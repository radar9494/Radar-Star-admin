package com.liuqi.base;

import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.utils.DateTimeUtils;
import org.junit.Test;
import org.redisson.api.RLock;

public class RedissionLockTest extends BaseTest{

    static int num=0;
    @Test
    public void testLock(){
        try {
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("");
            Thread.sleep(10000L);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
        for(int i=0;i<10;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RLock lock =null;
                    try {
                         lock = RedissonLockUtil.lock("name1");
                            num++;
                         System.out.println(Thread.currentThread().getName()+"-获取到锁"+ DateTimeUtils.currentDate("HH:mm:ss"));
                         if(num==6) {
                             Thread.sleep(60000L);
                         }else{
                             Thread.sleep(5000L);
                         }
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        System.out.println(Thread.currentThread().getName()+"-释放锁"+ DateTimeUtils.currentDate("HH:mm:ss"));
                        RedissonLockUtil.unlock(lock);
                    }
                }
            }).start();
        }

        try {
            Thread.sleep(300000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
