package com.liuqi.redis;

import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.SmsConfigModelDto;
import com.liuqi.business.service.SmsConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 记录用户充值密码的次数
 */
@Component
@Slf4j
public class CodeRepository {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private SmsConfigService smsConfigService;


	/**
	 * 验证是否可发送
	 *
	 * @param request
	 * @param account
	 * @return
	 */
	public String checkSendCode(HttpServletRequest request, String account) {
		String msg="";
		SmsConfigModelDto config = smsConfigService.getConfig();
		if(!SwitchEnum.isOn(config.getOnoff())){
			return "未开启信息发送";
		}
		int day_mobile_count = config.getDay();
		int hour_mobile_count = config.getHour();
		int minute_mobile_count = config.getMinute();
		//是否能发送
		boolean canSend=false;
		//判断一分钟是否能发
		canSend=this.canContinue(account,"minute",60L,TimeUnit.SECONDS,minute_mobile_count);
		if(canSend){
			//判断一小时能否发
			canSend=this.canContinue(account,"hou",1L,TimeUnit.HOURS,hour_mobile_count);
			if(canSend){
				//判断一天能否发
				canSend=this.canContinue(account,"day",1L,TimeUnit.DAYS,day_mobile_count);
				if(!canSend){
					msg="超出每天限制"+hour_mobile_count+"条，请1天后再试";
				}
			}else{
				canSend=false;
				msg="超出每小时限制"+hour_mobile_count+"条，请1小时后再试";
			}
		}else{
			msg="超出每分钟限制"+minute_mobile_count+"条，请1分钟后再试";
		}
		log.info("短信：" + account + "发送结果：" + canSend);
		return msg;
	}

	/**
	 * 是否能访问
	 * @return
	 */
	public boolean canContinue(String account,String type,Long time,TimeUnit unit,int total ){
		boolean canContinue=false;
		String key="limit:"+account+":"+type;
		String countStr=stringRedisTemplate.opsForValue().get(key);
		if(StringUtils.isEmpty(countStr)) {
			stringRedisTemplate.opsForValue().set(key, "1", time, unit);
			canContinue=true;
		}else{
			int count=Integer.valueOf(countStr);
			if(count<total){
				stringRedisTemplate.opsForValue().increment(key,1);
				canContinue=true;
			}
		}
		return canContinue;
	}

}
