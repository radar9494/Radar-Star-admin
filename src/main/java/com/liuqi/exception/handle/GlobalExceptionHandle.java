package com.liuqi.exception.handle;

import com.liuqi.exception.BaseException;
import com.liuqi.exception.BusinessException;
import com.liuqi.exception.DataParseException;
import com.liuqi.exception.NoLoginException;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandle {
    @ExceptionHandler({UnauthorizedException.class, UnauthenticatedException.class})
    public String unauthorizedException(HttpServletRequest request, Exception ex) {
        ex.printStackTrace();
        request.setAttribute("code", ReturnResponse.RETURN_FAIL);
        request.setAttribute("msg", ex.getMessage());
        request.setAttribute("obj", "");
        return "error";
    }

    @ResponseBody
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ReturnResponse illegalArgumentException(IllegalArgumentException ex) {
        ex.printStackTrace();
        String msg=ex.getMessage();
        msg= StringUtils.isNotEmpty(msg)&& msg.length()>100?msg.substring(0,100):msg;
        return ReturnResponse.backFail(msg);
    }

    @ExceptionHandler({NoLoginException.class})
    @ResponseBody
    public ReturnResponse noLoginException(Exception ex) {
        ex.printStackTrace();
        return ReturnResponse.backInfo(ReturnResponse.RETURN_NOLOGIN,"未获取到登录信息","");
    }

    @ExceptionHandler({BusinessException.class, DataParseException.class, BaseException.class})
    @ResponseBody
    public ReturnResponse businessException(Exception ex) {
        //ex.printStackTrace();
        String msg=ex.getMessage();
        msg= StringUtils.isNotEmpty(msg)&& msg.length()>100?msg.substring(0,100):msg;
        return ReturnResponse.backFail(msg);
    }

    /**
     * 全局异常捕捉处理
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ReturnResponse exception(Exception ex) {
        ex.printStackTrace();
        String msg=ex.getMessage();
        msg= StringUtils.isNotEmpty(msg)&& msg.length()>100?msg.substring(0,100):msg;
        return ReturnResponse.backFail(msg);
    }
}
