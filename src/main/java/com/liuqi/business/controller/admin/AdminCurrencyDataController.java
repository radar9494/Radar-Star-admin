package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.CurrencyDataModel;
import com.liuqi.business.model.CurrencyDataModelDto;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.service.CurrencyDataService;
import com.liuqi.business.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/currencyData")
public class AdminCurrencyDataController extends BaseAdminController<CurrencyDataModel, CurrencyDataModelDto> {

    @Autowired
    private CurrencyDataService currencyDataService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "admin";
    //模块
    private final static String BASE_MODUEL = "currencyData";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "add";

    @Override
    public BaseService getBaseService() {
        return this.currencyDataService;
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
    public String getDefaultExportName() {
        return DEFAULT_EXPORTNAME;
    }

    /*******待修改  排序  导出**********************************************************************************************************/
    //默认导出名称
    private final static String DEFAULT_EXPORTNAME = "币种介绍";

    @Override
    public String[] getDefaultExportHeaders() {
        String[] headers = {"主键", "更新时间", "更新时间", "备注", "版本号", "币种id", "中文名", "英文名", "简介", "发行时间", "发行数量", "流通总量", "众筹价格", "白皮书", "官网"};
        return headers;
    }

    @Override
    public String[] getDefaultExportColumns() {
        String[] columns = {"id", "createTime", "updateTime", "remark", "version", "currencyId", "ch", "en", "introduction", "issue", "issueQuantity", "circulate", "price", "white", "website"};
        return columns;
    }

    /*******自己代码**********************************************************************************************************/
    @Autowired
    private CurrencyService currencyService;
    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<CurrencyModelDto> list = currencyService.getAll();
        modelMap.put("list", list);
    }

}
