package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.UserSysStatusEnum;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.UserSysModel;
import com.liuqi.business.model.UserSysModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.UserSysService;
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

@Controller
@RequestMapping("/admin/userSys")
public class AdminUserSysController extends BaseAdminController<UserSysModel,UserSysModelDto> {

    @Autowired
    private UserSysService userSysService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="userSys";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
        return this.userSysService;
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
    private final static String DEFAULT_EXPORTNAME="承运商";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","名称","密码","状态","币种"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id", "createTime", "updateTime", "remark", "version", "name", "pwd", "statusStr", "currencyName"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Override
    protected String getNotIncludeField() {
        return super.getNotIncludeField() + "pwd,";
    }

    @Autowired
    private CurrencyService currencyService;

    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    @Override
    protected void toAddHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    @Override
    protected void toUpdateHandle(UserSysModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap) {
        List<CurrencyModelDto> list = currencyService.getAll();
        modelMap.put("list", list);
        modelMap.put("statusList", UserSysStatusEnum.getList());

    }

    @RequestMapping("/toModifyPwd")
    public String toModifyPwd(@RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request,
                              HttpServletResponse response) {
        modelMap.put("sysId", id);
        return getJspBasePath() + "/" + getBaseModuel() + "/modifyPwd";
    }

    @RequestMapping("/modifyPwd")
    @ResponseBody
    public ReturnResponse modifyPwd(@RequestParam("sysId") Long sysId, String newPwd, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(newPwd)) {
            return ReturnResponse.backFail("输入密码为空", "");
        }
        try {
            userSysService.modifyPwd(sysId, newPwd);
            return ReturnResponse.backSuccess();
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
