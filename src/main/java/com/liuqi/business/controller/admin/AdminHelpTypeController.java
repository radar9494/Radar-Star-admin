package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.HelpTypeStatusEnum;
import com.liuqi.business.model.HelpTypeModel;
import com.liuqi.business.model.HelpTypeModelDto;
import com.liuqi.business.service.HelpTypeService;
import com.liuqi.response.ReturnResponse;
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
@RequestMapping("/admin/helpType")
public class AdminHelpTypeController extends BaseAdminController<HelpTypeModel,HelpTypeModelDto> {

    @Autowired
    private HelpTypeService helpTypeService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="helpType";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.helpTypeService;
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
    private final static String DEFAULT_EXPORTNAME="帮助类型";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","父类id","名称","位置","0不启用 1启用"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","parentId","name","position","status"};
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
    protected void toUpdateHandle(HelpTypeModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
        t.setParentName(helpTypeService.getNameById(t.getId(),false));
    }

    @Override
    protected void toViewHandle(HelpTypeModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
        List<SelectDto> statusList= HelpTypeStatusEnum.getList();
        modelMap.put("statusList",statusList);

        List<HelpTypeModelDto> typeList= helpTypeService.getFirstLevel();
        modelMap.put("typeList",typeList);
    }


    @RequestMapping("/getByTypeId")
    @ResponseBody
    public ReturnResponse getByTypeId(@RequestParam(value = "typeId",defaultValue = "0")Long typeId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        try {
            List<HelpTypeModelDto> list=helpTypeService.getSub(typeId);
            return ReturnResponse.backSuccess(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }
    }
}
