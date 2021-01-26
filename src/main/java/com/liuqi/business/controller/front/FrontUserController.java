package com.liuqi.business.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.liuqi.anno.admin.CurAdminId;
import com.liuqi.anno.user.CurUserId;
import com.liuqi.base.BaseFrontController;
import com.liuqi.base.LoginUserTokenHelper;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.UserPayPayTypeEnum;
import com.liuqi.business.enums.UserPayStatusEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.NoLoginException;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.redis.CodeCache;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import com.liuqi.third.google.GoogleAuthService;
import com.liuqi.utils.Base64Util;
import com.liuqi.utils.ShiroPasswdUtil;
import com.liuqi.utils.ValidatorUtil;
import io.shardingsphere.api.HintManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(description ="用户" )
@Controller
@RequestMapping("/front/user")//（前台）用户控制层
public class FrontUserController extends BaseFrontController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private UserLevelService userLevelService;
    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserPayService userPayService;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private GoogleAuthService googleAuthService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private CustomerFeedbackService customerFeedbackService;
    @Autowired
    private OtcApplyService otcApplyService;
    @Autowired
    private OtcApplyConfigService otcApplyConfigService;


    /**
     * 用户基本信息
     *
     * @return
     */
    @ApiOperation(value = "用户基本信息")
    @PostMapping(value = "/baseInfo")
    @ResponseBody
    public ReturnResponse userInfo( HttpServletRequest request, ModelMap modelMap)throws NoLoginException {
        Long userId= LoginUserTokenHelper.getUserId(request);
        UserModelDto user = userService.getNotPwdById(userId);
        return ReturnResponse.backSuccess(user);
    }
    /**
     * 用户认证基本信息
     *
     * @return
     */
    @ApiOperation(value = "用户认证基本信息")
    @PostMapping(value = "/authInfo")
    @ResponseBody
    public ReturnResponse authInfo(HttpServletRequest request, ModelMap modelMap)throws NoLoginException {
        Long userId = super.getUserId(request);
        UserAuthModelDto auth = userAuthService.getByUserId(userId);
        if (StringUtils.isEmpty(auth.getRemark())) {
            auth.setRemark(MessageSourceHolder.getMessage("errorMessage7"));
        }
        UserModelDto user = userService.getNotPwdById(userId);
        JSONObject obj=new JSONObject();
        obj.put("user",user);
        obj.put("auth",auth);
        return ReturnResponse.backSuccess(obj);
    }

    /**
     * 前台更新用户的登录密码
     * @param request
     * @param pwd
     * @param newPwd
     * @param reNewPwd
     * @param code
     * @return
     */
    @ApiOperation(value = "前台更新用户的登录密码")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="pwd" ,value = "旧密码",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="newPwd" ,value = "新密码",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="reNewPwd" ,value = "新确认密码",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="code" ,value = "短信",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="Integer",name="type" ,value = "类型 0短信 1助记词",required = true,paramType = "query"),
    })
    @PostMapping("/changePwd")
    @ResponseBody
    public ReturnResponse changePwd(HttpServletRequest request,@RequestParam("pwd") String pwd,@RequestParam("newPwd")  String newPwd,
                                    @RequestParam("reNewPwd") String reNewPwd, @RequestParam("code") String code,
                                                @RequestParam("type") Integer type) throws NoLoginException {
        if(StringUtils.isAnyBlank(pwd,newPwd,reNewPwd)){
           return  ReturnResponse.backFail("参数异常");
        }
        if(!StringUtils.equals(newPwd,reNewPwd)){
            return  ReturnResponse.backFail("两次输出的新密码不相等!");
        }
        if(newPwd.length()>20||newPwd.length()<6){
            return  ReturnResponse.backFail("新密码输入长度有误！");
        }
        Long userId = super.getUserId(request);
        boolean flag=true;
        if(type==1){
            flag=false;
        }
        userService.changePwd(userId, newPwd, pwd, flag,code);
        return ReturnResponse.backSuccess();
    }

    /**
     * 前台更新用户的交易密码
     * @param tradePassword
     * @param newTradePassword
     * @param reNewTradePassword
     * @param code
     * @param request
     * @return
     */
    @ApiOperation(value = "前台更新用户的交易密码")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="tradePassword" ,value = "旧密码",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="newTradePassword" ,value = "新密码",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="reNewTradePassword" ,value = "新确认密码",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="code" ,value = "短信  助记词",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="Integer",name="type" ,value = "类型  0短信 1助记词",required = true,paramType = "query")
    })
    @PostMapping(value = "/changeTradePwd")
    @ResponseBody
    public ReturnResponse changeTradePwd(
                                         @RequestParam("newTradePassword") String newTradePassword,
                                         @RequestParam("reNewTradePassword") String reNewTradePassword,
                                         @RequestParam("code") String code,
                                         @RequestParam("type") Integer  type,
                                         HttpServletRequest request) throws NoLoginException {
        if(StringUtils.isAnyBlank(newTradePassword,reNewTradePassword)){
            return  ReturnResponse.backFail("参数异常");
        }
        if(!StringUtils.equals(newTradePassword,reNewTradePassword)){
            return  ReturnResponse.backFail("两次输出的新密码不相等!");
        }
        if(newTradePassword.length()>20||newTradePassword.length()<6){
            return  ReturnResponse.backFail("新密码输入长度有误！");
        }
        Long userId = super.getUserId(request);
        boolean flag=true;
        if(type==1){
            flag=false;
        }

        userService.changeTradePwd(newTradePassword,userId,flag,code);
        return ReturnResponse.backSuccess();
    }


//    @ApiOperation(value = "认证第一步")
//    @ApiImplicitParams({
//            @ApiImplicitParam(dataType = "String", name = "realName", value = "正实姓名", required = true, paramType = "query"),
//            @ApiImplicitParam(dataType = "String", name = "idcart", value = "省份证号", required = true, paramType = "query")
//    })
//    @PostMapping("/approveOne")
//    @ResponseBody
//    public ReturnResponse approveOne(HttpServletRequest request, UserAuthModel userAuthModel) throws NoLoginException {
//        Long userId = super.getUserId(request);
//        if (userAuthModel == null) {
//            return  ReturnResponse.backFail("参数异常");
//        }
//        if (StringUtils.isAnyBlank(userAuthModel.getRealName(), userAuthModel.getIdcart())) {
//            return  ReturnResponse.backFail("参数异常");
//        }
//        userAuthService.approveOne(userId, userAuthModel);
//        return ReturnResponse.backSuccess();
//    }
    @ApiOperation(value = "认证")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "realName", value = "正实姓名", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "idcart", value = "省份证号", required = true, paramType = "query"),
            @ApiImplicitParam(dataType ="MultipartFile",name="file1" ,value = "正面",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="MultipartFile",name="file2" ,value = "反面",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="MultipartFile",name="file3" ,value = "手持",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="image1" ,value = "正面地址1",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="image2" ,value = "正面地址2",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="image3" ,value = "正面地址3",required = true,paramType = "query")
    })
    @PostMapping("/approveTwo")
    @ResponseBody
    public ReturnResponse approveTwo(@RequestParam(value = "file1",required = false)MultipartFile file1,
                                          @RequestParam(value = "file2",required = false)MultipartFile file2,
                                          @RequestParam(value = "file3",required = false)MultipartFile file3,
                                          @RequestParam(value = "image1",required = false,defaultValue = "") String image1,
                                          @RequestParam(value = "image2",required = false,defaultValue = "") String image2,
                                          @RequestParam(value = "image3",required = false,defaultValue = "") String image3,
                                          @RequestParam(value = "realName",required = false,defaultValue = "") String realName,
                                          @RequestParam(value = "idcart",required = false,defaultValue = "") String idcart,
                                          HttpServletRequest request)throws NoLoginException {
        try {
            Long userId = super.getUserId(request);
            UserAuthModel auth = userAuthService.getByUserId(userId);
            auth.setRealName(realName);
            auth.setIdcart(idcart);
            if(userAuthService.auth(userId)){
                return ReturnResponse.backFail("该账号已经申请认证了");
            }
            //第一个文件上传
            if("".equals(image1)) {
                    auth.setImage1(uploadFileService.uploadImg(file1,5));
            }
            if("".equals(image2)) {
                auth.setImage2(uploadFileService.uploadImg(file2,5));
            }
            if("".equals(image3)) {
                auth.setImage3(uploadFileService.uploadImg(file3,5));
            }
            userAuthService.approveTwo(auth);
            return ReturnResponse.backSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }

    }

    /**
     * 找回登录密码
     * @param newPwd
     * @param code
     * @param request
     * @return
     */
    @ApiOperation(value = "找回登录密码")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="newPwd" ,value = "新密码",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="code" ,value = "短信验证码",required = true,paramType = "query")
    })
    @PostMapping("/findLoginPwd")
    @ResponseBody
    public ReturnResponse findLoginPwd(@RequestParam("newPwd") String newPwd, @RequestParam("code") String code, HttpServletRequest request) throws NoLoginException {
        if(StringUtils.isAnyBlank(newPwd,code)){
            return  ReturnResponse.backFail("参数异常");
        }
        if(newPwd.length()>20||newPwd.length()<6){
            return  ReturnResponse.backFail("新密码输入长度有误！");
        }
        //获取当前用户的用户编号
        Long userId = super.getUserId(request);
        userService.findLoginPwd(newPwd,code,userId);
        return ReturnResponse.backSuccess();
    }

    /**
     * 找回交易密码
     * @param newTradePwd
     * @param code
     * @param request
     * @return
     */
    @ApiOperation(value = "找回交易密码")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="newTradePwd" ,value = "新密码",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="code" ,value = "短信验证码",required = true,paramType = "query")
    })
    @PostMapping("/findTradePwd")
    @ResponseBody
    public ReturnResponse findTradePwd(@RequestParam("newTradePwd") String newTradePwd, @RequestParam("code") String code, HttpServletRequest request) throws NoLoginException {
        if(StringUtils.isAnyBlank(newTradePwd,code)){
            return  ReturnResponse.backFail("参数异常");
        }
        if(newTradePwd.length()>20||newTradePwd.length()<6){
            return  ReturnResponse.backFail("新密码输入长度有误！");
        }
        //获取当前用户的用户编号
        Long userId = super.getUserId(request);
        userService.findTradePwd(newTradePwd,code,userId);
        return ReturnResponse.backSuccess();
    }

    /**
     * 获取信息
     * @param pageNum
     * @param pageSize
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取所有信息分页")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="pageNum" ,value = "当前页",required = false,paramType = "query",defaultValue = "1"),
            @ApiImplicitParam(dataType ="Integer",name="pageSize" ,value = "条数",required = false,paramType = "query",defaultValue = "20")
    })
    @PostMapping("/myMessage")
    @ResponseBody
    public ReturnResponse myMessage(@RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                    @RequestParam(defaultValue = "20", required = false) final Integer pageSize, HttpServletRequest request) throws NoLoginException {
        //获取当前用户的用户编号
        Long userId = super.getUserId(request);
        MessageModelDto search=new MessageModelDto();
        search.setUserId(userId);
        search.setSortName("status,t.create_time");
        PageInfo<MessageModelDto> list =messageService.queryFrontPageByDto(search,pageNum,pageSize);
        Integer count=messageService.getNotReadCount(userId);

        JSONObject obj=new JSONObject();
        obj.put("notReadCount",count);
        obj.put("list",list);
        return ReturnResponse.backSuccess(obj);
    }

    /**
     * 阅读信息
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "阅读信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Long",name="id" ,value = "信息id",required = true,paramType = "query")
    })
    @PostMapping("/readMessage")
    @ResponseBody
    public ReturnResponse readMessage(@RequestParam("id")  Long id,HttpServletRequest request) throws NoLoginException {
        //获取当前用户的用户编号
        Long userId = super.getUserId(request);
        messageService.readMessage(userId,id);
        return ReturnResponse.backSuccess();
    }


    /**
     * 获取收款列表
     *
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取收款列表")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20")
    })
    @PostMapping("/getPayList")
    @ResponseBody
    public ReturnResponse getPayList(@RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                     @RequestParam(defaultValue = "20", required = false) final Integer pageSize, HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        UserPayModelDto search=new UserPayModelDto();
        search.setUserId(userId);
        PageInfo<UserPayModelDto> list = userPayService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(list);
    }

    /**
     * 付款设置状态
     *
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "付款设置状态")
    @PostMapping("/toPayStatus")
    @ResponseBody
    public ReturnResponse toPayStatus(HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        Map<String, Object> params = new HashMap<>();
        UserPayModelDto search=new UserPayModelDto();
        search.setUserId(userId);
        List<UserPayModelDto> payList = userPayService.queryListByDto(search,true);
        boolean yhk=false;
        boolean zfb=false;
        boolean wx=false;
        if(payList!=null && payList.size()>0){
            for(UserPayModel pay:payList){
                if(pay.getPayType().equals(UserPayPayTypeEnum.YHK.getCode()) && UserPayStatusEnum.USING.getCode().equals(pay.getStatus())){
                    yhk=true;
                }else if(pay.getPayType().equals(UserPayPayTypeEnum.ZFB.getCode()) && UserPayStatusEnum.USING.getCode().equals(pay.getStatus())){
                    zfb=true;
                }else if(pay.getPayType().equals(UserPayPayTypeEnum.WX.getCode()) && UserPayStatusEnum.USING.getCode().equals(pay.getStatus())){
                    wx=true;
                }
            }
        }
        JSONObject obj=new JSONObject();
        obj.put("yhk",yhk);
        obj.put("wx",wx);
        obj.put("zfb",zfb);
        return ReturnResponse.backSuccess(obj);
    }

    /**
     * 获取收款信息
     *
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "收款信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "payType", value = "收款类型 1银行卡 2支付宝 3微信", required = true, paramType = "query")
    })
    @PostMapping("/payInit")
    @ResponseBody
    public ReturnResponse payInit(@RequestParam(value = "payType",defaultValue = "1") Integer payType, HttpServletRequest request) throws NoLoginException {
        //强制使用主库
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();

        //获取当前用户的用户编号
        Long userId = super.getUserId(request);
        UserPayModelDto pay = userPayService.getByUserId(userId, payType);
        if (pay == null) {
            userPayService.init(userId, payType);
            //查询一次
            pay = userPayService.getByUserId(userId, payType);
        }
        return ReturnResponse.backSuccess(pay);
    }

    /**
     * 设置收款列表
     *
     * @param file
     * @param image
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "设置收款列表")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "MultipartFile", name = "file1", value = "图片", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "image1", value = "图片地址", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "Stirng", name = "name", value = "姓名", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Stirng", name = "no", value = "账号", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "Stirng", name = "bankAddress", value = "银行地址", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "Stirng", name = "bankName", value = "银行名称", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "int", name = "status", value = "状态", required = false, paramType = "query"),
    })
    @PostMapping("/updatePay")
    @ResponseBody
    public ReturnResponse updatePay(@RequestParam(value = "file", required = false) MultipartFile file,
                                    @RequestParam(value = "image", required = false, defaultValue = "") String image,
                                    UserPayModelDto userPay,
                                    HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        UserPayModelDto pay = userPayService.getByUserId(userId,userPay.getPayType());
        //银行卡没有图片
        if (!UserPayPayTypeEnum.YHK.getCode().equals(pay.getPayType()) && "".equals(image)) {
            pay.setPic( uploadFileService.uploadImg(file,5));
        }
        pay.setNo(userPay.getNo());
        pay.setBankAddress(userPay.getBankAddress());
        pay.setBankName(userPay.getBankName());
        pay.setName(userPay.getName());
        pay.setStatus(userPay.getStatus());
        userPayService.update(pay);
        return ReturnResponse.backSuccess();
    }

    /**
     * 手机认证
     *
     * @param request
     * @param code
     * @return
     */
    @ApiOperation(value = "手机认证")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "phone", value = "手机号", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "zone", value = "区号", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "code", value = "验证码", required = true, paramType = "query")
    })
    @PostMapping("/phoneAuth")
    @ResponseBody
    public ReturnResponse phoneAuth(HttpServletRequest request, @RequestParam("phone") String phone,
                                    @RequestParam("code") String code, @RequestParam(value = "zone", defaultValue = "86") String zone) throws NoLoginException {
        Long userId = super.getUserId(request);
        if (StringUtils.isEmpty(phone) || !ValidatorUtil.isMobile(phone)) {
            return ReturnResponse.backFail("手机输入有误");
        }
        UserModel user = userService.getById(userId);
        if (user.getPhoneAuth().equals(YesNoEnum.YES.getCode())) {
            return ReturnResponse.backFail("已认证");
        }
        CodeCache.verifyCode(phone, code);

        userService.phoneAuth(userId, phone, zone);
        return ReturnResponse.backSuccess();
    }

    /**
     * 邮箱认证
     *
     * @param request
     * @param code
     * @return
     */
    @ApiOperation(value = "邮箱认证")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "email", value = "邮箱", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "code", value = "验证码", required = true, paramType = "query"),
    })
    @PostMapping("/emailAuth")
    @ResponseBody
    public ReturnResponse emailAuth(HttpServletRequest request, @RequestParam("email") String email, @RequestParam("code") String code) throws NoLoginException {
        Long userId = super.getUserId(request);
        if (StringUtils.isEmpty(email) || !ValidatorUtil.isEmail(email)) {
            return ReturnResponse.backFail("邮箱输入有误");
        }
        UserModel user = userService.getById(userId);
        if (user.getEmailAuth().equals(YesNoEnum.YES.getCode())) {
            return ReturnResponse.backFail("已认证");
        }
        CodeCache.verifyCode(email, code);

        userService.emailAuth(userId, email);
        return ReturnResponse.backSuccess();
    }

    /**
     * 更换认证手机
     *
     * @param request
     * @param code
     * @return
     */
    @ApiOperation(value = "更换认证手机")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "newPhone", value = "手机号", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "zone", value = "区号", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "code", value = "老验证码", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "newCode", value = "新验证码", required = true, paramType = "query")
    })
    @PostMapping("/changePhone")
    @ResponseBody
    public ReturnResponse changePhone(HttpServletRequest request, @RequestParam("newPhone") String newPhone,
                                      @RequestParam("code") String code,
                                      @RequestParam("newCode") String newCode,    @RequestParam(value = "zone", defaultValue = "86") String zone) throws NoLoginException {
        Long userId = super.getUserId(request);
        if (StringUtils.isEmpty(newPhone) || !ValidatorUtil.isMobile(newPhone)) {
            return ReturnResponse.backFail("手机输入有误");
        }
        UserModel user = userService.getById(userId);
        CodeCache.verifyCode(user.getPhone(), code);
        CodeCache.verifyCode(newPhone ,newCode);
        userService.phoneAuth(userId, newPhone, zone);
        return ReturnResponse.backSuccess();
    }

    /**
     * 邮箱认证
     *
     * @param request
     * @param code
     * @return
     */
    @ApiOperation(value = "修改邮箱认证")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "newEmail", value = "邮箱", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "code", value = "老邮箱验证码", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "code1", value = "新邮箱验证码", required = true, paramType = "query")
    })
    @PostMapping("/changeEmail")
    @ResponseBody
    public ReturnResponse changeEmail(HttpServletRequest request, @RequestParam("newEmail") String newEmail,
                                      @RequestParam("code") String code,
                                      @RequestParam("code1") String code1
                                       ) throws NoLoginException {
        Long userId = super.getUserId(request);
        UserModel user = userService.getById(userId);
        if (StringUtils.isEmpty(newEmail) || !ValidatorUtil.isEmail(newEmail)) {
            return ReturnResponse.backFail("邮箱输入有误");
        }
//        String key=KeyConstant.KEY_USER_VERIFY+userId;
//        String uuidTemp = redisRepository.getString(key);
//        if(!uuid.equals(uuidTemp)){
//            return ReturnResponse.backFail("验证已过期");
//        }
        CodeCache.verifyCode(user.getEmail(), code);
        CodeCache.verifyCode(newEmail, code1);
        userService.emailAuth(userId, newEmail);
        return ReturnResponse.backSuccess();
    }

    /**
     * 修改验证码接受类型
     *
     * @param request
     * @param authType
     * @return
     */
    @ApiOperation(value = "修改验证码接受类型")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "authType", value = "接受方式 0手机 1邮件", required = true, paramType = "query")
    })
    @PostMapping("/changeAuthType")
    @ResponseBody
    public ReturnResponse changeAuthType(HttpServletRequest request, @RequestParam(value = "authType", defaultValue = "0") Integer authType) throws NoLoginException {
        Long userId = super.getUserId(request);
        UserModel user = userService.getById(userId);
        if (authType.equals(UserModelDto.AUTHTYPE_PHONE)) {
            if (user.getPhoneAuth().equals(YesNoEnum.YES.getCode())) {
                user.setAuthType(UserModelDto.AUTHTYPE_PHONE);
            } else {
                return ReturnResponse.backFail("手机未认证");
            }
        } else {
            if (user.getEmailAuth().equals(YesNoEnum.YES.getCode())) {
                user.setAuthType(UserModelDto.AUTHTYPE_EMAIL);
            } else {
                return ReturnResponse.backFail("邮箱未认证");
            }
        }
        userService.update(user);
        return ReturnResponse.backSuccess();
    }

    /**
     * 修改otc名称
     *
     * @param request
     * @param code
     * @return
     */
    @ApiOperation(value = "修改otc名称")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "name", value = "otc名称", required = true, paramType = "query"),
    })
    @PostMapping("/changeOtcName")
    @ResponseBody
    public ReturnResponse changeOtcName(HttpServletRequest request, @RequestParam("name") String name, @RequestParam("code") String code) throws NoLoginException {
        Long userId = super.getUserId(request);
        UserModel user = userService.getById(userId);
        user.setOtcName(name);
        userService.update(user);
        return ReturnResponse.backSuccess();
    }


    /**
     * 我的邀请
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "我的邀请")
    @PostMapping("/myInvite")
    @ResponseBody
    public ReturnResponse myInvite(HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        //下1级
        List<Long> subList=userLevelService.getAssignSubIdList(userId,1);
        //所有下级人数
        List<Long> teamList=userLevelService.getAllSubIdList(userId);
        JSONObject obj=new JSONObject();
        obj.put("subCount",subList!=null?subList.size():0);
        obj.put("teamCount",teamList!=null?teamList.size():0);
        return ReturnResponse.backSuccess(obj);
    }

    /**
     * 邀请会员列表
     * @param pageNum
     * @param pageSize
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "邀请会员列表")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="pageNum" ,value = "当前页",required = false,paramType = "query",defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20")
    })
    @PostMapping(value = "/myInviteList")
    @ResponseBody
    public ReturnResponse myInviteList(@RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                               @RequestParam(defaultValue = "20", required = false) final Integer pageSize,HttpServletRequest request)throws NoLoginException  {
        Long userId = super.getUserId(request);
        UserLevelModelDto search=new UserLevelModelDto();
        search.setParentId(userId);
        PageInfo<UserLevelModelDto> page=userLevelService.queryFrontPageByDto(search,pageNum,pageSize);
        return ReturnResponse.backSuccess(page);
    }


    /**
     * 我的分享地址
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "我的分享地址")
    @PostMapping(value = "/shareAddress")
    @ResponseBody
    public ReturnResponse shareAddress(HttpServletRequest request)throws NoLoginException  {
        Long userId = super.getUserId(request);
        UserModel user=userService.getById(userId);
        JSONObject obj=new JSONObject();
        obj.put("address", configService.getProjectAddress()+ "/register.html?leaderName=" + Base64Util.encodeToString(user.getInviteCode()));
        return ReturnResponse.backSuccess(obj);
    }


    @ApiOperation(value = "激活")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "phone", value = "下级账号", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "tradePwd", value = "交易密码", required = true, paramType = "query"),

    })
    @PostMapping("/active")
    @ResponseBody
    public ReturnResponse active(String phone ,@CurUserId Long userId, HttpServletRequest request,String tradePwd,String code,Long gooleCode
    ) {
        UserModel userModel = userService.queryByName(phone);
        userService.checkTradePssword(userId,tradePwd);
        Preconditions.checkNotNull(userModel, "账号不存在");
        userService.active(userId,phone,true);
        return ReturnResponse.backSuccess();
    }

    @ApiOperation(value = "用户反馈")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "phone", value = "联系方式", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "name", value = "联系人", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "content", value = "内容", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "title", value = "标题", required = true, paramType = "query"),
    })
    @PostMapping("/feedback")
    @ResponseBody
    public ReturnResponse feedback(CustomerFeedbackModel model ,@CurUserId Long userId, HttpServletRequest request) {
        model.setUserId(userId);
        customerFeedbackService.feedback(model);
        return ReturnResponse.backSuccess();
    }

    @ApiOperation(value = "承运商申请")
    @ApiImplicitParams({
    })
    @PostMapping("/otcApply")
    @ResponseBody
    public ReturnResponse otcApply(@CurUserId Long userId, HttpServletRequest request) {
        otcApplyService.apply(userId);
        return ReturnResponse.backSuccess();
    }

    @ApiOperation(value = "获取申请承运商状态")
    @ApiImplicitParams({
    })
    @PostMapping("/getOtcApplyStatus")
    @ResponseBody
    public ReturnResponse getOtcApplyStatus(@CurUserId Long userId, HttpServletRequest request) {
        return ReturnResponse.backSuccess( otcApplyService.getOtcApplyStatus(userId));
    }





    @ApiOperation(value = "配置")
    @ApiImplicitParams({
    })
    @PostMapping("/getOtcApplyConfig")
    @ResponseBody
    public ReturnResponse getOtcApplyConfig(@CurUserId Long userId, HttpServletRequest request) {
        return ReturnResponse.backSuccess(otcApplyConfigService.getConfig());
    }

    @ApiOperation(value = "谷歌认证")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "googleCode", value = "谷歌验证码", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Long", name = "code", value = "邮箱验证码", required = true, paramType = "query"),
    })
    @PostMapping("/googleAuth")
    @ResponseBody
    public ReturnResponse googleAuth(HttpServletRequest request, @RequestParam("googleCode") Long googleCode, @RequestParam("code") String code) throws NoLoginException {
        Long userId = super.getUserId(request);
        UserModel user = userService.getById(userId);
        if (user.getGoogleAuth().equals(YesNoEnum.YES.getCode())) {
            return ReturnResponse.backFail("已认证");
        }
        CodeCache.verifyCode(user.getEmail(), code);
        boolean verify = googleAuthService.verify(user.getGoogleSecret(), googleCode);
        if(!verify){
            return ReturnResponse.backFail("谷歌验证码错误");
        }
        user.setGoogleAuth(YesNoEnum.YES.getCode());
        userService.update(user);
        return ReturnResponse.backSuccess();
    }


    @PostMapping("/setPayType")
    @ApiOperation(value = "设置支付方式")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "payType", value = "支付类型 0 交易密码 1交易密码+谷歌验证码 2交易密码+邮箱验证码  3交易密码加谷歌验证码加邮箱验证码", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "code", value = "验证码", required = true, paramType = "query"),

    })
    @ResponseBody
    public ReturnResponse setPayType(HttpServletRequest request, @RequestParam("payType") Integer payType,@Param("code") String code) throws NoLoginException {
        Long userId = super.getUserId(request);
        UserModel user = userService.getById(userId);
        user.setPayType(payType);
        CodeCache.verifyCode(user.getEmail(),code);
        userService.update(user);
        return ReturnResponse.backSuccess();
    }


    @PostMapping("/setTradeFree")
    @ApiOperation(value = "设置交易免密类型")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "tradeFree", value = "  ", required = true, paramType = "query"),

    })
    @ResponseBody
    public ReturnResponse setTradeFree(HttpServletRequest request, @RequestParam("tradeFree") Integer tradeFree,@Param("code") String code) throws NoLoginException {
        Long userId = super.getUserId(request);
        UserModel user = userService.getById(userId);
        user.setTradeFree(tradeFree);
        CodeCache.verifyCode(user.getEmail(),code);
        userService.update(user);
        // 0每次都输入 1 两小时不用 2 6小时不用 12小时不用 永远不用
        Integer hour=0;
         if(tradeFree==1){
            hour=2;
        }else if(tradeFree==2){
            hour=6;
        }else if(tradeFree==3){
            hour=12;
        }else{
            hour=0;
        }
       if(tradeFree==0){
           redisRepository.del(KeyConstant.KEY_TRADE_TRAEDE_FREE+userId);
       }
        else if(hour==0){
            redisRepository.set(KeyConstant.KEY_TRADE_TRAEDE_FREE+userId,"1");
        }else{
            redisRepository.set(KeyConstant.KEY_TRADE_TRAEDE_FREE+userId,"1",hour, TimeUnit.HOURS);
        }
        return ReturnResponse.backSuccess();
    }


    @PostMapping("/getTradeFree")
    @ApiOperation(value = "获取交易是否输入")
    @ApiImplicitParams({
    })
    @ResponseBody
    public ReturnResponse getTradeFree(HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        return ReturnResponse.backSuccess(redisRepository.getString(KeyConstant.KEY_TRADE_TRAEDE_FREE+userId));
    }

    @PostMapping("/setInfo")
    @ApiOperation(value = "设置信息")
    @ApiImplicitParams({
    })
    @ResponseBody
    public ReturnResponse setInfo(HttpServletRequest request,String name,String phoneCode,String email,String code,String pwd,String tradePwd) throws NoLoginException {
        Long userId = super.getUserId(request);
        userService.setInfo(userId,name,email,code,pwd,tradePwd,phoneCode);
        return ReturnResponse.backSuccess();
    }


}
