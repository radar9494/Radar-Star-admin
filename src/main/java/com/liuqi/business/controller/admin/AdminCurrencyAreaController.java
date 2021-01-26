package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.enums.UsingEnum;
import com.liuqi.business.model.CurrencyAreaModel;
import com.liuqi.business.model.CurrencyAreaModelDto;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.service.CurrencyAreaService;
import com.liuqi.business.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/currencyArea")
public class AdminCurrencyAreaController extends BaseAdminController<CurrencyAreaModel, CurrencyAreaModelDto> {

    @Autowired
    private CurrencyAreaService currencyAreaService;
    @Autowired
    private CurrencyService currencyService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="currencyArea";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.currencyAreaService;
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
    private final static String DEFAULT_EXPORTNAME="币种区域";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","区域名称","显示位置","状态","币种"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","name","position","statusStr","currencyName"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Override
    protected void toAddHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    @Override
    protected void toUpdateHandle(CurrencyAreaModelDto currencyAreaModel, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap) {
        List<CurrencyModelDto> list = currencyService.getAll();
        modelMap.put("list",list);
        modelMap.put("statusList", UsingEnum.getList());
    }

}
