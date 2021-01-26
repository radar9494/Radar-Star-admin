package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.FinancingConfigGrantTypeEnum;
import com.liuqi.business.enums.FinancingConfigStatusEnum;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.FinancingConfigModel;
import com.liuqi.business.model.FinancingConfigModelDto;
import com.liuqi.business.model.FinancingIntroduceModel;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.FinancingConfigService;
import com.liuqi.business.service.FinancingIntroduceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
@Controller
@RequestMapping("/admin/financingConfig")
public class AdminFinancingConfigController extends BaseAdminController<FinancingConfigModel,FinancingConfigModelDto> {

    @Autowired
    private FinancingConfigService financingConfigService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="financingConfig";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.financingConfigService;
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
    private final static String DEFAULT_EXPORTNAME="融资融币配置";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","币种id","总额度","已融资额度","融资币种id","兑换比例（1融资币种：币种）","开始时间","结束时间","每次最小数量","每次最大数量","发放类型 0直接发放 1结束统一发放","状态 0未开始 1进行中 2结束"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","currencyId","quantity","curQuantity","financingCurrencyId","exchange","startTime","endTime","min","max","grantType","status"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/

    @Autowired
    private CurrencyService currencyService;
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
    protected void toUpdateHandle(FinancingConfigModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(FinancingConfigModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
        List<SelectDto> grantTypeList= FinancingConfigGrantTypeEnum.getList();
        modelMap.put("grantTypeList",grantTypeList);

        List<SelectDto> statusList = FinancingConfigStatusEnum.getList();
        modelMap.put("statusList",statusList);

        List<CurrencyModelDto> currencyList= currencyService.getAll();
        modelMap.put("currencyList",currencyList);
    }

    @Override
    protected void addHandle(FinancingConfigModelDto t, Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.addHandle(t, curUserId, modelMap, request, response);
        //设置默认状态为未开始
        t.setStatus(FinancingConfigStatusEnum.NOTSTART.getCode());
    }

}
