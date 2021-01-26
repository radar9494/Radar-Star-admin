package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseConstant;
import com.liuqi.base.BaseService;
import com.liuqi.base.LoginAdminUserHelper;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.UserAdminStatusEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.model.RoleModelDto;
import com.liuqi.business.model.UserAdminModel;
import com.liuqi.business.model.UserAdminModelDto;
import com.liuqi.business.service.RoleService;
import com.liuqi.business.service.UserAdminService;
import com.liuqi.response.ReturnResponse;
import com.liuqi.third.google.GoogleAuthService;
import com.liuqi.third.google.GoogleAuthenticator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/userAdmin")
public class AdminUserAdminController extends BaseAdminController<UserAdminModel, UserAdminModelDto> {

    @Autowired
    private UserAdminService userAdminService;
    @Autowired
    private RoleService roleService;

    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="userAdmin";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
        return this.userAdminService;
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
    private final static String DEFAULT_EXPORTNAME="管理员";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"编码"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private GoogleAuthService googleAuthService;

    @Override
    public void toAddHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<RoleModelDto> roles = roleService.queryListByDto(new RoleModelDto(),false);
        modelMap.put("list",roles);
        modelMap.put("statusList",UserAdminStatusEnum.getList());
    }

    @Override
    public void toUpdateHandle(UserAdminModelDto user, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<RoleModelDto> roles = roleService.queryListByDto(new RoleModelDto(),false);
        modelMap.put("list",roles);
        modelMap.put("statusList", UserAdminStatusEnum.getList());
    }
    @Override
    protected String getNotIncludeField() {
        return super.getNotIncludeField() + "pwd,";
    }

    /**
     * @param userAdminModel
     * @param modelMap
     * @param request
     * @param response
     */
    @Override
    public void addHandle(UserAdminModelDto userAdminModel,Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        userAdminModel.setSecret(GoogleAuthenticator.generateSecretKey());
        userAdminModel.setAuth(YesNoEnum.NO.getCode());
    }

    @RequestMapping("/toModifyPwd")
    public String toModifyPwd(@RequestParam("id") Long id,ModelMap modelMap, HttpServletRequest request,
                              HttpServletResponse response) {
        modelMap.put("adminId",id);
        return getJspBasePath() + "/" + getBaseModuel() + "/modifyPwd";
    }
    @RequestMapping("/modifyPwd")
    @ResponseBody
    public ReturnResponse modifyPwd(@RequestParam("adminId") Long adminId, String newPwd, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(newPwd)) {
            return ReturnResponse.backFail("输入密码为空","");
        }
        try {
            userAdminService.modifyPwd(adminId,newPwd);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            return ReturnResponse.backFail(e.getMessage());
        }
    }

    @RequestMapping("/toModifyPwdByOld")
    public String toModifyPwd(ModelMap modelMap, HttpServletRequest request,
                              HttpServletResponse response) {
        return "/admin/modifyPwd";
    }

    @RequestMapping("/modifyPwdByOld")
    @ResponseBody
    public ReturnResponse modifyPwdByOld(@RequestParam("old") String old,
                                         @RequestParam("pwd") String pwd,
                                         @RequestParam("pwd2") String pwd2,
                                         ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isAnyBlank(old,pwd,pwd2)) {
            return ReturnResponse.backFail("密码为空", "");
        }
        if (!pwd.equals(pwd2)) {
            return ReturnResponse.backFail("确认密码不一致", "");
        }
        Long adminId= LoginAdminUserHelper.getAdminId();
        try {
            userAdminService.modifyPwdByOld(adminId,old, pwd);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            return ReturnResponse.backFail(e.getMessage());
        }
    }

    @RequestMapping("/toBind")
    public String toBind(@RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request,
                         HttpServletResponse response) {
        modelMap.put("adminId", id);
        UserAdminModelDto admin = userAdminService.getById(id);
        if(StringUtils.isEmpty(admin.getSecret())){
            admin.setSecret(GoogleAuthenticator.generateSecretKey());
            admin.setAuth(YesNoEnum.NO.getCode());
            userAdminService.update(admin);
        }
        String bingdName = BaseConstant.BASE_PROJECT + ":" + admin.getName();
        modelMap.put("admin", admin);
        modelMap.put("bindName", bingdName);
        modelMap.put("qr", GoogleAuthenticator.getQRBarcode(bingdName, admin.getSecret()));
        return getJspBasePath() + "/" + getBaseModuel() + "/modifyBind";
    }

    @RequestMapping("/modifyBind")
    @ResponseBody
    public ReturnResponse modifyBind(@RequestParam("adminId") Long adminId, @RequestParam("code") Long code, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        try {
            UserAdminModelDto admin = userAdminService.getById(adminId);
            boolean verify = googleAuthService.verify(admin.getSecret(), code);
            if (verify) {
                admin.setAuth(YesNoEnum.YES.getCode());
                userAdminService.update(admin);
                return ReturnResponse.backSuccess();
            }
            return ReturnResponse.backFail();
        } catch (Exception e) {
            return ReturnResponse.backFail(e.getMessage());
        }
    }

    @Override
    protected boolean addLogger() {
        return true;
    }

    @Override
    protected boolean updateLogger() {
        return true;
    }
}
