package com.liuqi.business.controller.front;

import com.liuqi.base.BaseFrontController;
import com.liuqi.base.FrontValid;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.model.UserModelDto;
import com.liuqi.business.service.ConfigService;
import com.liuqi.business.service.UserService;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.redis.CodeCache;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.ValidatorUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 前台注册控制层
 */
@Api(description ="注册" )
@RestController
@RequestMapping("/front")
public class RegisterController extends BaseFrontController {

    @Autowired
    private UserService userService;
    @Autowired
    private ConfigService configService;
    @ApiOperation(value = "注册")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="pwd" ,value = "密码",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="tradePwd" ,value = "交易密码",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="name" ,value = "账号",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="zone" ,value = "区号",required = false,defaultValue = "86",paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="email" ,value = "邮箱",required = true,paramType = "query")
    })
    @PostMapping("/register")
     public ReturnResponse Register(@Validated(FrontValid.class) UserModelDto user, @RequestParam("code") String code,BindingResult bindingResult,
                                    HttpServletRequest request){
        if (bindingResult.hasErrors()) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("param_error")+":"+getErrorInfo(bindingResult));
        }
        String reSwitch =configService.queryValueByName(ConfigConstant.CONFIGNAME_REGISTER_SWITCH);
        if (!SwitchEnum.isOn(reSwitch)) {
            return ReturnResponse.backFail("暂未开放注册");
        }

        if(!ValidatorUtil.isEmail(user.getEmail())){
            return ReturnResponse.backFail("请输入正确邮箱");
        }
        UserModel userTemp = userService.queryByName(user.getName());
        if (userTemp != null) {
            return ReturnResponse.backFail("账号已存在");
        }


        //注册是否必填邀请码
//        String mustParent =configService.queryValueByName(ConfigConstant.CONFIGNAME_REGISTER_INVITECODE);
//        if(StringUtils.isEmpty(user.getParentName())
//                && SwitchEnum.isOn(mustParent)) {
//            return ReturnResponse.backFail("注册必填邀请码");
//        }

       //CodeCache.verifyCode(user.getEmail(),code);
        userService.register(user);
        return ReturnResponse.backSuccess();
     }

    @ApiOperation(value = "注册检查")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="code" ,value = "验证码",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="email" ,value = "邮箱",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="name" ,value = "账号",required = true,paramType = "query")
    })
    @PostMapping("/registerCheck")
    public ReturnResponse registerCheck(@RequestParam("email") String email ,@RequestParam("name") String name , @RequestParam("code") String code,
                                   HttpServletRequest request){

        String reSwitch =configService.queryValueByName(ConfigConstant.CONFIGNAME_REGISTER_SWITCH);
        if (!SwitchEnum.isOn(reSwitch)) {
            return ReturnResponse.backFail("暂未开放注册");
        }
        if(!ValidatorUtil.isEmail(email)){
            return ReturnResponse.backFail("请输入正确邮箱");
        }
        UserModel userTemp = userService.queryByName(name);
        if (userTemp != null) {
            return ReturnResponse.backFail("账号已存在");
        }
        CodeCache.verifyCode(email,code);
        return ReturnResponse.backSuccess();
    }

}
