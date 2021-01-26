package com.liuqi.business.controller.admin;


import com.liuqi.anno.admin.CurAdminId;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.ListingApplyModel;
import com.liuqi.business.model.ListingApplyModelDto;
import com.liuqi.business.model.UserModelDto;
import com.liuqi.business.service.ListingApplyService;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.ListingApplyStatusEnum;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/listingApply")
public class AdminListingApplyController extends BaseAdminController<ListingApplyModel,ListingApplyModelDto> {

    @Autowired
    private ListingApplyService listingApplyService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="listingApply";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.listingApplyService;
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
    private final static String DEFAULT_EXPORTNAME="上币申请";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","联系人手机号","联系人姓名","币种中文名称","币种英文名称","总发现量","市场已流通量","社区用户量","营销预算","项目介绍","状态"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","phone","realName","currencyNameCn","currencyNameEn","total","liquidity","communityCount","marketingBudget","projectIntroduction","status"};
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
    protected void toUpdateHandle(ListingApplyModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(ListingApplyModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
        List<SelectDto> statusList= ListingApplyStatusEnum.getList();
        modelMap.put("statusList",statusList);

    }



    @RequestMapping("/updateStatus")
    @ResponseBody
    public ReturnResponse updateStatus(Long id, Integer status, @CurAdminId Long adminId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        return listingApplyService.updateStatus(id,status,adminId);
    }



}
