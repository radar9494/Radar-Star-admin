package com.liuqi.business.controller.admin;


import com.liuqi.anno.admin.CurAdminId;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.base.LoginAdminUserHelper;
import com.liuqi.business.model.MiningWalletModel;
import com.liuqi.business.model.OtcApplyModel;
import com.liuqi.business.model.OtcApplyModelDto;
import com.liuqi.business.service.OtcApplyService;
import com.liuqi.business.service.UserService;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import com.liuqi.business.dto.SelectDto;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/otcApply")
public class AdminOtcApplyController extends BaseAdminController<OtcApplyModel,OtcApplyModelDto> {

    @Autowired
    private OtcApplyService otcApplyService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="otcApply";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.otcApplyService;
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
    private final static String DEFAULT_EXPORTNAME="承兑商申请";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","用户","状态"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","userId","status"};
        return columns;
    }

    @Autowired
    private UserService userService;

    @Override
    protected void listHandle(OtcApplyModelDto searchDto, HttpServletRequest request) {
        super.listHandle(searchDto, request);
        if(StringUtils.isNotEmpty(searchDto.getUserName())){
            searchDto.setUserId(userService.queryIdByName(searchDto.getUserName()));
        }
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
    protected void toUpdateHandle(OtcApplyModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(OtcApplyModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
    }


    @RequestMapping("/audit")
    @ResponseBody
    public ReturnResponse audit(Long id, @CurAdminId Long adminId, HttpServletRequest request) {
        Long userId = LoginAdminUserHelper.getAdminId();
        otcApplyService.audit(id,adminId);
        return ReturnResponse.backSuccess();
    }

    @RequestMapping("/refuse")
    @ResponseBody
    public ReturnResponse refuse(Long id, @CurAdminId Long adminId, HttpServletRequest request) {
        Long userId = LoginAdminUserHelper.getAdminId();
        otcApplyService.refuse(id,adminId);
        return ReturnResponse.backSuccess();
    }


}
