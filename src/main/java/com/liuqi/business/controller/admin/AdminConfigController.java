package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.ConfigTypeEnum;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.ConfigModel;
import com.liuqi.business.model.ConfigModelDto;
import com.liuqi.business.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/config")
public class AdminConfigController extends BaseAdminController<ConfigModel, ConfigModelDto> {
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="config";
    @Autowired
    private ConfigService configService;
    @Override
    public BaseService getBaseService() {
        return this.configService;
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
    public String getNotOperate() {
        return NOT_OPERATE;
    }


    @Override
    public String getDefaultExportName() {return DEFAULT_EXPORTNAME;}

    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    /*******待修改  排序  导出**********************************************************************************************************/
    //默认导出名称
    private final static String DEFAULT_EXPORTNAME="配置";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"名称","备注","值","类型"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"name","remarks","val","typeStr"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Override
    protected boolean updateLogger() {
        return true;
    }

    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    @Override
    protected void toAddHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    @Override
    protected void toUpdateHandle(ConfigModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap) {
        modelMap.put("typeList", ConfigTypeEnum.getList());
    }
}
