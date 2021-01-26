package com.liuqi.redis;

import com.liuqi.base.BaseConstant;
import com.liuqi.business.dto.CaptchaDto;
import com.liuqi.exception.BusinessException;
import com.liuqi.utils.MethodLimit;
import com.wf.captcha.ArithmeticCaptcha;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author tanyan
 *
 * https://gitee.com/whvse/EasyCaptcha
 *
 * @create 2020-01=07
 * @description
 */
@Component
public class Captcha {

    private static RedisRepository redisRepository;
    private static MethodLimit methodLimit;

    @Autowired
    public void setStringRepository(RedisRepository redisRepository) {
        Captcha.redisRepository = redisRepository;
    }
    @Autowired
    public void setMethodLimit(MethodLimit methodLimit) {
        Captcha.methodLimit = methodLimit;
    }

    public static Boolean hashKey(String key){
        key = "CAPTCHA:" + key;
        return redisRepository.hasKey(key);
    }

    public static CaptchaDto saveCaptcha(String key){
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 48);
        captcha.setLen(2);  // 几位数运算，默认是两位
        //captcha.getArithmeticString();  // 获取运算的公式：3+2=?
        String verCode=captcha.text();  // 获取运算的结果：5
        redisRepository.set("CAPTCHA:"+key, verCode, Long.valueOf(BaseConstant.KAPTCHA_TIME), TimeUnit.SECONDS);
        return new CaptchaDto(key,captcha.toBase64());
    }

    public static void checkCaptcha(String key,String code){
        key = "CAPTCHA:" + key;
        String capText = redisRepository.getString(key);
        if (StringUtils.isNotEmpty(capText) && capText.equals(code)) {
            redisRepository.del(key);
            //清除验证次数
            methodLimit.clean("verifyCaptcha", key);
        }else {
            //验证成功后清除验证码
            redisRepository.del(key);
            //清除验证次数
            methodLimit.clean("verifyCode", key);
            throw new BusinessException("验证失败");
        }
    }

}
