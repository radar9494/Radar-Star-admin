package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.base.LoginAdminUserHelper;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.UserAuthEnum;
import com.liuqi.business.enums.UserPayPayTypeEnum;
import com.liuqi.business.model.UserAuthModel;
import com.liuqi.business.model.UserAuthModelDto;
import com.liuqi.business.service.UserAuthService;
import com.liuqi.business.service.UserService;
import com.liuqi.response.ReturnResponse;
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
import java.util.Map;

@Controller
@RequestMapping("/admin/userAuth")
public class AdminUserAuthController extends BaseAdminController<UserAuthModel,UserAuthModelDto> {

    @Autowired
    private UserAuthService userAuthService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="userAuth";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add";
    @Override
    public BaseService getBaseService() {
    	return this.userAuthService;
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
    public String getNotOperate() { return NOT_OPERATE;}
    @Override
    public String getDefaultExportName() { return DEFAULT_EXPORTNAME;}
    /*******待修改  排序  导出**********************************************************************************************************/
    //默认导出名称
    private final static String DEFAULT_EXPORTNAME="用户认证";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键", "创建时间", "用户", "真实姓名", "身份证", "状态"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id", "createTime", "userName", "realName", "idcart", "authStatusStr"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/

    @Autowired
    private UserService userService;
    @Override
    protected void listHandle(UserAuthModelDto dto, HttpServletRequest request) {
        //设置用户
        super.listHandle(dto, request);
        //设置用户
        String userName=dto.getUserName();
        if(StringUtils.isNotEmpty(userName)){
            Long userId=userService.queryIdByName(userName);
            dto.setUserId(userId);
        }
    }
    @Override
    protected String getNotIncludeField() {
        return super.getNotIncludeField()+"image1,image2,image3,authStatus,userId,";
    }

    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<SelectDto> authStatusList= UserAuthEnum.getList();
        modelMap.put("authStatusList",authStatusList);
    }

    @RequestMapping("/toAuth")
    public String toAuth(@RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request,
                              HttpServletResponse response) {
        UserAuthModel auth=userAuthService.getById(id);
        modelMap.put("auth",auth);
        return getJspBasePath() + "/" + getBaseModuel() + "/auth";
    }

    /**
     * 认证
     * @param userId
     * @param status
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/auth")
    @ResponseBody
    public ReturnResponse auth(@RequestParam("userId") Long userId,@RequestParam("status") Integer status,
                               @RequestParam("remark") String remark, ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) {
        try {
            Long adminId= LoginAdminUserHelper.getAdminId();
            userAuthService.authUser(userId, status,remark,adminId);
            return ReturnResponse.backSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }
    }

    @RequestMapping("/auths")
    @ResponseBody
    public ReturnResponse auths( String ids,
                                ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) {

        String []idss=ids.split(",");

        try {
            Long adminId= LoginAdminUserHelper.getAdminId();
            for(String s:idss) {
                Long id=Long.parseLong(s);
                UserAuthModel auth=userAuthService.getById(id);
                userAuthService.authUser(auth.getUserId(), UserAuthEnum.SUCCESS.getCode(), "批量审核", adminId);
            }
            return ReturnResponse.backSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }
    }


}
