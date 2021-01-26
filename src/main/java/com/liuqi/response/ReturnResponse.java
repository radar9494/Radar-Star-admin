package com.liuqi.response;

import com.alibaba.fastjson.JSONObject;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 响应信息
 */
@Builder
@Data
public class ReturnResponse<T> implements Serializable {

	//成功
	public static final int RETURN_OK = 0;
	//失败
	public static final int RETURN_FAIL = -1;
	//未登录
	public static final int RETURN_NOLOGIN = -2;
	//需要认证
	public static final int RETURN_AUTH = -3;
	//冻结
	public static final int RETURN_FROST = -4;
	//冻结
	public static final int RETURN_VERIFY= -5;
	//更新
	public static final int RETURN_UPDATE= -6;

	private int code;

	private String msg;

	private T obj;

	private Object other;//附加字段
	//返回时间
	private Long time;

	/**
	 * 返回成功
	 * @return
	 */
	public static ReturnResponse backSuccess() {
		return ReturnResponse.backInfo(ReturnResponse.RETURN_OK, "处理成功", "");
	}
	/**
	 * 返回成功
	 * @return
	 */
	public static <T> ReturnResponse backSuccess(String msg, T obj) {
		return ReturnResponse.backInfo(ReturnResponse.RETURN_OK, msg, obj);
	}

	/**
	 * 返回成功
	 *
	 * @return
	 */
	public static <T> ReturnResponse backSuccess(T obj) {
		return ReturnResponse.backInfo(ReturnResponse.RETURN_OK, "处理成功", obj);
	}

	/**
	 * 返回失败
	 *
	 * @return
	 */
	public static ReturnResponse backFail() {
		return ReturnResponse.backInfo(ReturnResponse.RETURN_FAIL, "处理失败", "");
	}

	/**
	 * 返回失败
	 *
	 * @return
	 */
	public static ReturnResponse backFail(String msg) {
		return ReturnResponse.backInfo(ReturnResponse.RETURN_FAIL, msg, "");
	}

	/**
	 * 返回失败
	 *
	 * @return
	 */
	public static <T> ReturnResponse backFail(String msg, T obj) {
		return ReturnResponse.backInfo(ReturnResponse.RETURN_FAIL, msg, obj);
	}

	/**
	 * 返回失败
	 *
	 * @return
	 */
	public static <T> ReturnResponse backInfo(Integer code, String msg, T obj) {
		return ReturnResponse.builder().code(code).msg(msg).obj(obj).time(System.currentTimeMillis()).build();
	}

	public String toJson(){
		return 	JSONObject.toJSONString(this);
	}
}
