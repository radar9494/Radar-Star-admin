package com.liuqi.anno.sys;

import com.liuqi.anno.admin.CurAdminId;
import com.liuqi.base.LoginAdminUserHelper;
import com.liuqi.base.LoginSysUserHelper;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurSysIDMethodArgumentResolver implements HandlerMethodArgumentResolver{

    public CurSysIDMethodArgumentResolver() {}

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(CurSysId.class);
    }

    @Nullable
    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, @Nullable WebDataBinderFactory webDataBinderFactory) throws Exception {
        return LoginSysUserHelper.getSysId();
    }
}
