package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.model.SuperNodeConfigModel;
import com.liuqi.business.model.SuperNodeConfigModelDto;
import com.liuqi.business.model.UserModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.SuperNodeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/superNodeConfig")
public class AdminSuperNodeConfigController extends BaseAdminController<SuperNodeConfigModel,SuperNodeConfigModelDto> {

    @Autowired
    private SuperNodeConfigService superNodeConfigService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="superNodeConfig";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add";
    @Override
    public BaseService getBaseService() {
    	return this.superNodeConfigService;
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
    private final static String DEFAULT_EXPORTNAME="超级节点配置";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","分红开关0关 1开","总手续费分红比例%","分红结算币种","节点总人数","参与开关0关 1开","参与扣除币种","参与扣除币种数量"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","releaseOnoff","releaseRate","releaseCurrencyId","count","joinOnoff","joinCurrencyId","joinQuantity"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/

    @Autowired
    private CurrencyService currencyService;
    private void getEnumList(ModelMap modelMap) {
        modelMap.put("currencyList", currencyService.getAll());
        modelMap.put("switchList", SwitchEnum.getList());
    }

    @RequestMapping("/toConfig")
    public String toConfig(ModelMap modelMap, HttpServletRequest request,
                        HttpServletResponse response) {
        SuperNodeConfigModelDto m=superNodeConfigService.getConfig();
        modelMap.put("m",m);
        this.getEnumList(modelMap);
        return getJspBasePath() + "/" + getBaseModuel() + "/"+getBaseModuel()+"Config";
    }
}
