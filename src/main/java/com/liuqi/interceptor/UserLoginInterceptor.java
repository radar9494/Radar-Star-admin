package com.liuqi.interceptor;


import ch.qos.logback.core.util.CloseUtil;
import cn.hutool.json.JSONUtil;
import com.liuqi.base.BaseConstant;
import com.liuqi.base.LoginUserTokenHelper;
import com.liuqi.business.enums.UserStatusEnum;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.service.UserService;
import com.liuqi.response.ReturnResponse;
import com.liuqi.token.RedisTokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员登录拦截器
 */
public class UserLoginInterceptor extends HandlerInterceptorAdapter {


	@Autowired
	private UserService userService;
	@Autowired
	private RedisTokenManager redisTokenManager;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Long userId=null;
		//获取当前用户的编号
		try {
			userId = LoginUserTokenHelper.getUserId(request);
			if (userId <=0) {
				this.response(request, response, ReturnResponse.RETURN_NOLOGIN, "登录已失效，请重新登录");
				return false;
			}
		}catch (Exception e){
			//e.printStackTrace();
			this.response(request, response, ReturnResponse.RETURN_NOLOGIN, "登录已失效，请重新登录");
			return false;
		}

		//刷新token
		String token = request.getHeader(BaseConstant.TOKEN_NAME);
		redisTokenManager.refreshUserToken(token);

		UserModel user = userService.getById(userId);
		/**
		 * 查询用户的状态 如果用户未激活 得跳转到完善用户信息界面
		 */
		if (!UserStatusEnum.USDING.getCode().equals(user.getStatus())) {
			redisTokenManager.loginOff(token);
			//ajax返回
			this.response(request, response, ReturnResponse.RETURN_FROST, "用户冻结，请联系管理员解除冻结");
			return false;
		}
		return super.preHandle(request, response, handler);
	}


	/**
	 * 响应信息
	 *
	 * @param request
	 * @param response
	 * @param message  响应的信息
	 * @return
	 */
	public void response(HttpServletRequest request, HttpServletResponse response, Integer code, String message) {
		ReturnResponse returnResponse = ReturnResponse.builder()
				.code(code).msg(message).build();
		response.setContentType("application/json;charset=UTF-8");
		ServletOutputStream outputStream = null;
		try {
			outputStream = response.getOutputStream();
			outputStream.write(JSONUtil.toJsonStr(returnResponse).getBytes());
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			CloseUtil.closeQuietly(outputStream);
		}
	}

}