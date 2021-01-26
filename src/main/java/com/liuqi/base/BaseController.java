package com.liuqi.base;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseController{




	public String getErrorInfo(BindingResult bindingResult){
		StringBuffer errors=new StringBuffer();
		for(ObjectError error: bindingResult.getAllErrors()){
			errors.append(error.getDefaultMessage()).append(";");
		}
		return errors.toString();
	}

	/**
	 * 判断请求是否为Ajax
	 * @param request     HttpServletRequest
	 * @return             true or false
	 */
	public  boolean isAjaxRequest(HttpServletRequest request) {
		String ajaxPostReqHead = request.getHeader("x-requested-with");
		String ajaxGetReqHead = request.getHeader("RequestType");
		return (StringUtils.isNotEmpty(ajaxPostReqHead) && ajaxPostReqHead.equals("XMLHttpRequest"))
				|| (StringUtils.isNotEmpty(ajaxGetReqHead) && ajaxGetReqHead.equalsIgnoreCase("ajax"));
	}

}
