package com.liuqi.redis;

import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.exception.BusinessException;
import com.liuqi.utils.MethodLimit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 用户验证码cache
 */
@Component
public class CodeCache {


	private static RedisRepository redisRepository;
	private static MethodLimit methodLimit;
	@Autowired
	public void setStringRepository(RedisRepository redisRepository) {
		CodeCache.redisRepository = redisRepository;
	}

	@Autowired
	public void setMethodLimit(MethodLimit methodLimit) {
		CodeCache.methodLimit = methodLimit;
	}

	@Value("${spring.profiles.active}")
	private static String active;
	/**
	 * 存储验证码
	 *
	 * @param account
	 * @param code
	 */
	public static void storeCode(String account, String code) {
		redisRepository.set(KeyConstant.KEY_USER_AUTH+account,code,10L, TimeUnit.MINUTES);
	}
	/**
	 * 获取验证码
	 *
	 * @param account
	 */
	public static String getCode(String account) {
		return redisRepository.getString(KeyConstant.KEY_USER_AUTH+account);
	}
	/**
	 * 校验验证码
	 *
	 * @param account
	 * @param currentCode
	 */
	public static void verifyCode(String account, String currentCode) {
		/** 测试环境取消验证邮箱 */
		if ("test".equals(active)){
			return;
		}
		String code = redisRepository.getString(KeyConstant.KEY_USER_AUTH+account);
//		if(StringUtils.isEmpty(code)){
//			throw new BusinessException("无效验证码");
//		}
		//10分钟只能5次
		int residueTimes = methodLimit.residueTimes("verifyCode", account, 5, 10L, TimeUnit.MINUTES);
		if (residueTimes > 0) {
			if (StringUtils.isNotBlank(code) && StringUtils.equalsIgnoreCase(currentCode, code)) {
				//验证成功后清除验证码
				redisRepository.del(KeyConstant.KEY_USER_AUTH + account);
				//清除验证次数
				methodLimit.clean("verifyCode", account);
			}
//			else if(currentCode.equals("123456")){
//				redisRepository.del(KeyConstant.KEY_USER_AUTH + account);
//				//清除验证次数
//				methodLimit.clean("verifyCode", account);
//			}
			else {
				throw new BusinessException("验证码错误,剩余次数" + (residueTimes - 1));
			}
		} else {
			//验证成功后清除验证码
			redisRepository.del(KeyConstant.KEY_USER_AUTH + account);
			//清除验证次数
			methodLimit.clean("verifyCode", account);
			throw new BusinessException("验证码失效");
		}


	}
}
