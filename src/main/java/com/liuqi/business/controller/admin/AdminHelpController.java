package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.HelpStatusEnum;
import com.liuqi.business.model.HelpModel;
import com.liuqi.business.model.HelpModelDto;
import com.liuqi.business.model.HelpTypeModelDto;
import com.liuqi.business.service.HelpService;
import com.liuqi.business.service.HelpTypeService;
import com.liuqi.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin/help")
public class AdminHelpController extends BaseAdminController<HelpModel,HelpModelDto> {

    @Autowired
    private HelpService helpService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="help";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.helpService;
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
    private final static String DEFAULT_EXPORTNAME="帮助管理";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","类型id","标题","内容","0不启用 1启用"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","typeId","title","content","status"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/

    @Autowired
    private HelpTypeService helpTypeService;

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
    protected void addHandle(HelpModelDto t, Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.addHandle(t, curUserId, modelMap, request, response);
        if(t.getTypeId()==null || t.getTypeId()<=0){
            throw new BusinessException("请选择类型");
        }
    }

    @Override
    protected void updateHandle(HelpModelDto t, Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.updateHandle(t, curUserId, modelMap, request, response);
        if(t.getTypeId()==null || t.getTypeId()<=0){
            throw new BusinessException("请选择类型");
        }
    }

    @Override
    protected void toUpdateHandle(HelpModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
        HelpTypeModelDto temp= helpTypeService.getById(t.getTypeId());
        HelpTypeModelDto parent = helpTypeService.getById(temp.getParentId());
        List<List<HelpTypeModelDto>> dataList = new ArrayList();
        List<Long> data = new ArrayList();
        data.add(temp.getId());
        dataList.add(helpTypeService.getSub(temp.getParentId()));
        int maxLevel=1;
        //查询数据
        while (parent != null) {
            maxLevel++;
            data.add(parent.getId());
            dataList.add(helpTypeService.getSub(parent.getParentId()));
            parent = helpTypeService.getById(parent.getParentId());
        }
        Collections.reverse(dataList);
        Collections.reverse(data);
        modelMap.put("maxLevel", maxLevel);
        modelMap.put("dataList", dataList);
        modelMap.put("data", data);
    }

    @Override
    protected void toViewHandle(HelpModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
        List<SelectDto> statusList= HelpStatusEnum.getList();
        modelMap.put("statusList",statusList);

        List<HelpTypeModelDto> typeList= helpTypeService.getFirstLevel();
        modelMap.put("typeList",typeList);

    }
}
