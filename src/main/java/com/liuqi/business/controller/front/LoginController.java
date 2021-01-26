package com.liuqi.business.controller.front;

import com.liuqi.anno.admin.CurAdminId;
import com.liuqi.anno.user.CurUserId;
import com.liuqi.base.BaseFrontController;
import com.liuqi.base.LoginUserTokenHelper;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.UserStatusEnum;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.model.UserModelDto;
import com.liuqi.business.model.VersionModel;
import com.liuqi.business.service.ConfigService;
import com.liuqi.business.service.UserService;
import com.liuqi.business.service.VersionService;
import com.liuqi.exception.BusinessException;
import com.liuqi.redis.CodeCache;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.ShiroPasswdUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 前台登录控制层
 */
@Api(description ="登录，忘记密码，退出" )
@RestController
@RequestMapping("/front")
public class LoginController extends BaseFrontController {
    @Autowired
    private UserService userService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private VersionService versionService;
    @Autowired
    private RedisRepository redisRepository;
    //前台登录
//    @ApiOperation(value = "登录")
//    @ApiImplicitParams({
//            @ApiImplicitParam(dataType ="String",name="name" ,value = "用户名",required = true,paramType = "query"),
//            @ApiImplicitParam(dataType ="String",name="password" ,value = "密码",required = true,paramType = "query")
//    })
//    @PostMapping(value = "/loginNV")
//    public ReturnResponse loginNV(@RequestParam("name") String name, @RequestParam("password") String password,
//                                  HttpServletRequest request, ModelMap modelMap) {
//        Map<String,Object> mpa=userService.login(name, password, request);
//        return ReturnResponse.backSuccess(mpa);
//    }

    //前台登录
    @ApiOperation(value = "登录")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="name" ,value = "用户名",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="password" ,value = "密码",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="version" ,value = "版本",required = true,paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "type", value = "类型0安卓 1ios", required = true, paramType = "query"),
    })
    @PostMapping(value = "/login")
    public ReturnResponse login(@RequestParam("name") String name, @RequestParam("password") String password,
                                @RequestParam("version") String version,
                                @RequestParam(value = "type", defaultValue = "0") Integer type,
                                String emailCode,
                                HttpServletRequest request, ModelMap modelMap) {
        VersionModel versionModel = versionService.getConfig();
        Map<String, Object> map = new HashMap<String, Object>();

        //是否必须升级
        boolean mustUpdate = !versionModel.getAndroidVersion().equalsIgnoreCase(version);
        String appAddress = versionModel.getAndroidAddress();
        String appVersion = versionModel.getAndroidVersion();
        String updateInfo = versionModel.getAndroidInfo();
        if (type == 1) {//ios
            mustUpdate = !versionModel.getIosVersion().equalsIgnoreCase(version);
            appAddress = versionModel.getIosAddress();
            appVersion = versionModel.getIosVersion();
            updateInfo = versionModel.getIosInfo();
        }
        if (mustUpdate) {
            map.put("appAddress", appAddress);
            map.put("appVersion", appVersion);
            map.put("updateInfo", updateInfo);
            return ReturnResponse.backInfo(ReturnResponse.RETURN_UPDATE, "更新", map);
        }
        Map<String,Object> mpa= userService.login(name, password, request,emailCode);
        return ReturnResponse.backSuccess(mpa);
    }


    @ApiOperation(value = "检测登录密码")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="name" ,value = "用户名",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="password" ,value = "密码",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="version" ,value = "版本",required = true,paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "type", value = "类型0安卓 1ios", required = true, paramType = "query"),
    })
    @PostMapping(value = "/checkPwd")
    public ReturnResponse checkPwd(@RequestParam("name") String name, @RequestParam("password") String password,
                                @RequestParam("version") String version,
                                @RequestParam(value = "type", defaultValue = "0") Integer type,
                                String emailCode,
                                HttpServletRequest request, ModelMap modelMap) {
        VersionModel versionModel = versionService.getConfig();
        Map<String, Object> map = new HashMap<String, Object>();

        //是否必须升级
        boolean mustUpdate = !versionModel.getAndroidVersion().equalsIgnoreCase(version);
        String appAddress = versionModel.getAndroidAddress();
        String appVersion = versionModel.getAndroidVersion();
        String updateInfo = versionModel.getAndroidInfo();
        if (type == 1) {//ios
            mustUpdate = !versionModel.getIosVersion().equalsIgnoreCase(version);
            appAddress = versionModel.getIosAddress();
            appVersion = versionModel.getIosVersion();
            updateInfo = versionModel.getIosInfo();
        }
        if (mustUpdate) {
            map.put("appAddress", appAddress);
            map.put("appVersion", appVersion);
            map.put("updateInfo", updateInfo);
            return ReturnResponse.backInfo(ReturnResponse.RETURN_UPDATE, "更新", map);
        }
        String key = KeyConstant.KEY_LOGIN_ERROR + name;
        Integer times = redisRepository.getInteger(key);
        if (times >= 3) {
            throw new BusinessException("密码错误3次，请15分钟后再试");
        }
        UserModel userModel = userService.queryByName(name);
        if (userModel != null) {
            if (userModel.getStatus() == 2) {
                throw new BusinessException("用户被冻结，请联系管理员！");
            }
            if (userModel.getStatus() == UserStatusEnum.NOTUSING.getCode()) {
                throw new BusinessException("请先激活！");
            }
            if (!StringUtils.equalsIgnoreCase(userModel.getPwd(),
                    ShiroPasswdUtil.getUserPwd(password))) {
                throw new BusinessException("密码错误！");
            }
        }else{
            throw new BusinessException("用户不存在");
        }
        return ReturnResponse.backSuccess();
    }



    //忘记密码
    @ApiOperation(value = "忘记密码")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="name" ,value = "用户名",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="email" ,value = "邮箱",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="pwd" ,value = "密码",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="code" ,value = "短信或者助记词",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="type" ,value = "0 验证码 1助记词",required = true,paramType = "query") ,
    })
    @PostMapping(value = "/forgetPassword")
    public ReturnResponse forgetPassword(@RequestParam("name") String name,@RequestParam("email") String email, @RequestParam("pwd") String pwd, @RequestParam("code") String code,Integer type) {
        userService.forgetPassword(name,pwd,code,type,email);
        return ReturnResponse.backSuccess();
    }
    @ApiOperation(value = "退出")
    @PostMapping(value = "/logout")
    public ReturnResponse logout(HttpServletRequest request){
        LoginUserTokenHelper.removeUser(request);
        return ReturnResponse.backSuccess();
    }


    @ApiOperation(value = "设置助记词")
    @PostMapping(value = "/setMnemonic")
    public ReturnResponse setMnemonic(HttpServletRequest request,@CurUserId Long userId,String mnemonic){
        UserModelDto usermodel = userService.getById(userId);
        usermodel.setIsRemember(1);
        userService.update(usermodel);
        return ReturnResponse.backSuccess();
    }
}
