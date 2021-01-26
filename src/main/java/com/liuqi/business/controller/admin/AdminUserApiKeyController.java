package com.liuqi.business.controller.admin;


import com.alibaba.fastjson.JSONObject;
import com.liuqi.anno.admin.CurAdminId;
import com.liuqi.base.AdminValid;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.UsingEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.UserApiKeyService;
import com.liuqi.business.service.UserService;
import com.liuqi.response.ReturnResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import com.liuqi.business.dto.SelectDto;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/userApiKey")
public class AdminUserApiKeyController extends BaseAdminController<UserApiKeyModel,UserApiKeyModelDto> {

    @Autowired
    private UserApiKeyService userApiKeyService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="userApiKey";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.userApiKeyService;
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
    private final static String DEFAULT_EXPORTNAME="用户api";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","用户id","","","状态0未启用 1启用"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","userId","apiKey","secretKey","status"};
        return columns;
    }



    /*******自己代码**********************************************************************************************************/



    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toListHandle(modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toAddHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toAddHandle(modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toUpdateHandle(UserApiKeyModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(UserApiKeyModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
        List<SelectDto> list = UsingEnum.getList();
        modelMap.put("statusList",list);
        List<CurrencyModelDto> all = currencyService.getAll();
        modelMap.put("currencyList",all);
    }

    @Autowired
    private UserService userService;
    @Autowired
    private CurrencyService currencyService;

    @RequestMapping("/addP")
    @ResponseBody
    public ReturnResponse addP(@CurAdminId Long adminId, String userName, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        Long userid=userService.queryIdByName(userName);
        if(userid==null||userid.intValue()==0){
            return ReturnResponse.backFail("用户不存在");
        }
        return ReturnResponse.backSuccess(   userApiKeyService.createApi(userid,UsingEnum.USING.getCode()));
    }


}
