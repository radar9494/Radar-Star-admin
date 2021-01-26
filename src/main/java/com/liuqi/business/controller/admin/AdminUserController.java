package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.base.LoginAdminUserHelper;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.UserAuthTypeEnum;
import com.liuqi.business.enums.UserStatusEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.model.UserAuthModel;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.model.UserModelDto;
import com.liuqi.business.service.*;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/user")
public class AdminUserController extends BaseAdminController<UserModel, UserModelDto> {

    @Autowired
    private UserService userService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="user";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add";
    @Override
    public BaseService getBaseService() {
        return this.userService;
    }

    @Override
    public String getJspBasePath() {
        return JSP_BASE_PTH;
    }

    @Override
    public String getBaseModuel() {
        return BASE_MODUEL;
    }
    @Override
    protected String getNotOperate() {
        return NOT_OPERATE;
    }
    @Override
    public String getDefaultExportName() {
        return DEFAULT_EXPORTNAME;
    }
    /*******待修改  排序  导出**********************************************************************************************************/
    //默认导出名称
    private final static String DEFAULT_EXPORTNAME="用户";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","昵称","密码","密码强度","交易密码","区号","手机号","手机验证","邮箱","邮件验证","状态 ","上次登录时间","是否白名单","验证类型0手机 1邮件","是否机器人"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","name","pwd","pwdStrengthStr","tradePwd","zone","phone","phoneAuthStr","email","emailAuthStr","statusStr","lastLoginTime","whiteifStr","authTypStr","robotStr"};
        return columns;
    }


    /*******自己代码**********************************************************************************************************/
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private ZoneService zoneService;


    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toListHandle(modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toAddHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toAddHandle(modelMap, request, response);
        this.getEnumList(modelMap);
        modelMap.put("zone",zoneService.getAll());
    }

    @Override
    protected void toUpdateHandle(UserModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
        modelMap.put("zone",zoneService.getAll());
    }

    private void getEnumList(ModelMap modelMap) {
        List<SelectDto> yesNoList = YesNoEnum.getList();
        modelMap.put("yesNoList", yesNoList);

        List<SelectDto> statusList = UserStatusEnum.getList();
        modelMap.put("statusList", statusList);

        modelMap.put("typeList", UserAuthTypeEnum.getList());

    }

    @Autowired
    private ConfigService configService;


    @Override
    public ReturnResponse update(Long adminId, UserModelDto t, BindingResult bindingResult, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("发送类型:"+t.getSendType());
//        String s = configService.queryValueByName("user.update.off");
//        if(s.equals(0)){
//            return ReturnResponse.backSuccess();
//        }
        return super.update(adminId, t, bindingResult, modelMap, request, response);
    }

    @RequestMapping("/adminAdd")
    @ResponseBody
    public ReturnResponse adminAdd(@Valid UserModelDto userModel, BindingResult bindingResult, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return ReturnResponse.builder().code(ReturnResponse.RETURN_FAIL).msg(this.getErrorInfo(bindingResult)).build();
        }
        try {
            userService.register(userModel);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }
    }

    @Override
    protected String getNotIncludeField() {
        return super.getNotIncludeField()+"pwd,tradePwd,pwdStrength,lastLoginTime,inviteCode,phoneAuth,emailAuth,";
    }
    @RequestMapping(value = "/freeze")
    @ResponseBody
    public ReturnResponse freeze(@RequestParam Long id,String remark, HttpServletRequest request) {
        //冻结
        try {
            Long adminId=LoginAdminUserHelper.getAdminId();
            userService.freeze(id,adminId);
            return ReturnResponse.backSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }
    }
    @RequestMapping(value = "/unfreeze")
    @ResponseBody
    public ReturnResponse unfreeze(@RequestParam Long id, String remark, HttpServletRequest request) {
        //解冻
        try {
            Long adminId=LoginAdminUserHelper.getAdminId();
            userService.unfreeze(id,adminId);
            return ReturnResponse.backSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }
    }
    @RequestMapping("/toModifyPwd")
    public String toModifyPwd(@RequestParam Long id,ModelMap modelMap){
        UserModel user= userService.getById(id);
        modelMap.put("m",user);
        return JSP_BASE_PTH+"/"+BASE_MODUEL+"/pwd";
    }
    @RequestMapping(value = "/modifyPwd")
    @ResponseBody
    public ReturnResponse modifyPwd(@RequestParam Long id, @RequestParam("pwd") String pwd,@RequestParam("pwd2") String pwd2, HttpServletRequest request) {
        if(StringUtils.isEmpty(pwd)){
            return ReturnResponse.backFail("密码不能为空");
        }
        if(!pwd.equals(pwd2)){
            return ReturnResponse.backFail("密码不一致");
        }
        try {
            Long adminId=LoginAdminUserHelper.getAdminId();
            userService.modifyPwd(id,pwd,adminId);
            return ReturnResponse.backSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }
    }
    @RequestMapping("/toModifyTradePwd")
    public String toModifyTradePwd(@RequestParam Long id,ModelMap modelMap){
        UserModel user= userService.getById(id);
        modelMap.put("m",user);
        return JSP_BASE_PTH+"/"+BASE_MODUEL+"/tradePwd";
    }
    @RequestMapping("/toShow")
    public String toShow(ModelMap modelMap){

        return JSP_BASE_PTH+"/"+BASE_MODUEL+"/userShow";
    }

    @RequestMapping(value = "/modifyTradePwd")
    @ResponseBody
    public ReturnResponse modifyTradePwd(@RequestParam Long id, @RequestParam("pwd") String pwd,@RequestParam("pwd2") String pwd2, HttpServletRequest request) {
        if(StringUtils.isEmpty(pwd)){
            return ReturnResponse.backFail("交易密码不能为空");
        }
        if(!pwd.equals(pwd2)){
            return ReturnResponse.backFail("交易密码不一致");
        }
        try {
            Long adminId=LoginAdminUserHelper.getAdminId();
            userService.modifyTradePwd(id,pwd,adminId);
            return ReturnResponse.backSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }
    }

    @Override
    protected boolean updateLogger() {
        return true;
    }
}
