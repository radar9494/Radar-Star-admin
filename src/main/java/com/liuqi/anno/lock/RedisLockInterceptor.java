package com.liuqi.anno.lock;

import com.liuqi.redis.lock.RedissonLockUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@Component
public class RedisLockInterceptor {


    @Pointcut("@annotation(com.liuqi.anno.lock.RedisLock)")
    public void redisLockAspect() {
    }

    @Around("redisLockAspect()")
    public Object lockAroundAction(ProceedingJoinPoint point) {
        MethodSignature signature=(MethodSignature)point.getSignature();
        Method method=signature.getMethod();
        RedisLock redis= method.getAnnotation(RedisLock.class);
        String key=redis.lockPrefix()+getLockValue(method.getParameterAnnotations(),point.getArgs());
        RLock lock = null;
        try{
            lock = RedissonLockUtil.lock(key);
            //操作数据
            return point.proceed();
        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            RedissonLockUtil.unlock(lock);
        }
        return null;
    }



    private String getLockValue(Annotation[][] annotations,Object[] args){
        StringBuffer value = new StringBuffer();
        int i=0;
        for (Annotation[] annotation1 : annotations) {
            for (Annotation annotation : annotation1) {
                if (annotation instanceof LockValue) {
                    value.append(args[i] != null ? "_" + args[i].toString() : "");
                }
            }
            i++;
        }
        return value.toString();
    }


}
