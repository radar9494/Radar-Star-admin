package com.liuqi.business.controller.front;

import com.liuqi.base.BaseFrontController;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.model.UserModelDto;
import com.liuqi.business.model.ZoneModel;
import com.liuqi.business.service.AuthCodeService;
import com.liuqi.business.service.UserService;
import com.liuqi.business.service.ZoneService;
import com.liuqi.exception.NoLoginException;
import com.liuqi.redis.CodeCache;
import com.liuqi.redis.CodeRepository;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.ValidatorUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码发送
 */
@Api(description ="验证码发送" )
@RestController
@RequestMapping(value = "/ajax")
@Slf4j
public class AuthCodeController extends BaseFrontController {

	@Autowired
	private AuthCodeService authCodeService;

	@Autowired
	private CodeRepository codeRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private ZoneService zoneService;

	/**
	 * 获取区号数据
	 *
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "获取区号")
	@PostMapping("/zoneList")
	@ResponseBody
	public ReturnResponse zoneList(HttpServletRequest request) {
		List<ZoneModel> list = zoneService.getAll();
		return ReturnResponse.backSuccess(list);
	}
	/**
	 * 验证短信验证码
	 *
	 * @param request
	 * @param code
	 * @return
	 */
	@ApiOperation(value = "验证短信验证码-输入接受号码")
	@ApiImplicitParams({
			@ApiImplicitParam(dataType = "String", name = "phoneEmail", value = "手机或邮箱", required = true, paramType = "query"),
			@ApiImplicitParam(dataType = "String", name = "zone", value = "区号", required = false, defaultValue = "86", paramType = "query"),
			@ApiImplicitParam(dataType = "String", name = "code", value = "验证码", required = true, paramType = "query")})
	@PostMapping("/validateCode")
	@ResponseBody
	public ReturnResponse validateCode(HttpServletRequest request,
									   @RequestParam("phoneEmail") String phoneEmail,
									   @RequestParam(value = "zone", defaultValue = "86") String zone,
									   @RequestParam("code") String code) {
		CodeCache.verifyCode(phoneEmail, code);
		//验证成功后返回一个唯一编码给用户
		String uuid = UUID.randomUUID().toString().replace("-", "");
		//报错一个uuid  用于验证
		redisRepository.set(KeyConstant.KEY_USER_VERIFY + phoneEmail, uuid, 10L, TimeUnit.MINUTES);
		return ReturnResponse.backSuccess(uuid);
	}

	/**
	 * 更换认证手机
	 *
	 * @param request
	 * @param code
	 * @return
	 */
	@ApiOperation(value = "验证短信验证码--默认接收号码")
	@ApiImplicitParams({
			@ApiImplicitParam(dataType = "String", name = "code", value = "验证码", required = true, paramType = "query") })
	@PostMapping("/validateMyCode")
	@ResponseBody
	public ReturnResponse validateMyCode(HttpServletRequest request, @RequestParam("code") String code)throws NoLoginException {
		Long userId = super.getUserId(request);
		UserModel user = userService.getById(userId);
		CodeCache.verifyCode(user.getEmail(), code);
		//验证成功后返回一个唯一编码给用户
		String uuid= UUID.randomUUID().toString().replace("-","");
		//报错一个uuid  用于验证
		redisRepository.set(KeyConstant.KEY_USER_VERIFY + userId, uuid, 10L, TimeUnit.MINUTES);
		return ReturnResponse.backSuccess(uuid);
	}
	/**
	 * 发送 验证码
	 *
	 * @param phoneEmail
	 */
	@ApiOperation(value = "发送验证码")
	@ApiImplicitParams({
			@ApiImplicitParam(dataType = "String", name = "phoneEmail", value = "手机号或邮箱", required = true, paramType = "query"),
			@ApiImplicitParam(dataType ="String",name="zone" ,value = "区号",required = true,paramType = "query")
	})
	@PostMapping(value = "/sendCode")
	@ResponseBody
	public ReturnResponse sendCode(@RequestParam("phoneEmail") String phoneEmail, @RequestParam(value = "zone", defaultValue = "86") String zone, HttpServletRequest request) {
		//检查输入的是否是手机或者邮箱
		if (ValidatorUtil.isMobile(phoneEmail) || ValidatorUtil.isEmail(phoneEmail)) {
			String msg = codeRepository.checkSendCode(request, phoneEmail);
			if (StringUtils.isEmpty(msg)) {
				String randomCode = RandomStringUtils.randomNumeric(6);
				CodeCache.storeCode(phoneEmail, randomCode);
				boolean isChain=true;
				//发送类型 1短信 2邮件
				Integer sendType= UserModelDto.AUTHTYPE_PHONE;
				//邮箱
				if (ValidatorUtil.isEmail(phoneEmail)) {
					isChain=true;
					sendType=UserModelDto.AUTHTYPE_EMAIL;
				}else{
					//非中国手机  添加区号发送
					if(!"86".equals(zone)){
						isChain=false;
						//发送时添加区号
						phoneEmail = zone + phoneEmail;
					}
				}
				authCodeService.sendVerifyCode(phoneEmail, isChain, randomCode, sendType);
				return ReturnResponse.builder().code(ReturnResponse.RETURN_OK).build();
			}
			return ReturnResponse.builder().code(ReturnResponse.RETURN_FAIL).msg(msg).build();
		}
		return ReturnResponse.builder().code(ReturnResponse.RETURN_FAIL).msg("请输入正确的邮箱").build();
	}
	 /**
	  * 发送 验证码给当前用户
	 */
	 @ApiOperation(value = "发送验证码给当前用户")
	@PostMapping(value = "/sendMyCode")
	@ResponseBody
	public ReturnResponse sendMyCode(HttpServletRequest request) throws NoLoginException {
		Long userId = super.getUserId(request);
		UserModel user= userService.getById(userId);
		 return sendCode(user, request);
	 }


	/**
	 * 指定登录名发送验证码  用户忘记密码
	 */
	@ApiOperation(value = "发送验证码给指定用户")
	@ApiImplicitParams({
			@ApiImplicitParam(dataType ="String",name="name" ,value = "用户",required = true,paramType = "query"),
	})
	@PostMapping(value = "/sendCodeByName")
	@ResponseBody
	public ReturnResponse sendCodeByName(@RequestParam("name") String name, HttpServletRequest request) throws NoLoginException {
		UserModel user = userService.queryByName(name);
		if (user == null) {
			ReturnResponse.builder().code(ReturnResponse.RETURN_FAIL).msg("用户不存在").build();
		}
		return sendCode(user, request);
	}


	private ReturnResponse sendCode(UserModel user, HttpServletRequest request) {
		//判断是否能发送
		String account = user.getName();
		String msg = codeRepository.checkSendCode(request, account);
		if (StringUtils.isEmpty(msg)) {
			String randomCode = RandomStringUtils.randomNumeric(6);
			//发送时报错用户的name唯一值

			//发送类型 1短信 2邮件
			Integer sendType = UserModelDto.AUTHTYPE_PHONE;
			boolean isChain = true;
			//邮箱
			if (user.getAuthType().equals(UserModelDto.AUTHTYPE_EMAIL)) {
				account = user.getEmail();
				isChain = true;
				sendType = UserModelDto.AUTHTYPE_EMAIL;
			} else {//短信
				account = user.getPhone();
				//非中国手机  添加区号发送
				if (!"86".equals(user.getZone())) {
					isChain = false;
					//发送时添加区号
					account = user.getZone() + account;
				}
			}
			CodeCache.storeCode(account, randomCode);
			System.out.println("account:"+account+",sendType:"+sendType);
			authCodeService.sendVerifyCode(account, isChain, randomCode, sendType);
			return ReturnResponse.builder().code(ReturnResponse.RETURN_OK).build();
		}
		return ReturnResponse.builder().code(ReturnResponse.RETURN_FAIL).msg(msg).build();
	}

}
