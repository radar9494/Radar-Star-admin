package com.liuqi.anno.user;

import com.liuqi.base.BaseConstant;
import com.liuqi.base.LoginUserTokenHelper;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurUserIDMethodArgumentResolver implements HandlerMethodArgumentResolver{

    public CurUserIDMethodArgumentResolver() {}

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(CurUserId.class);
    }

    @Nullable
    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, @Nullable WebDataBinderFactory webDataBinderFactory) throws Exception {
        return LoginUserTokenHelper.getUserIdByToken(nativeWebRequest.getHeader(BaseConstant.TOKEN_NAME));
    }
}
