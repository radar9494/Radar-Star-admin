package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.enums.WalletTypeEnum;
import com.liuqi.business.model.CurrencyConfigModel;
import com.liuqi.business.model.CurrencyConfigModelDto;
import com.liuqi.business.model.CurrencyModel;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.service.CurrencyConfigService;
import com.liuqi.business.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/currencyConfig")
public class AdminCurrencyConfigController extends BaseAdminController<CurrencyConfigModel, CurrencyConfigModelDto> {

    @Autowired
    private CurrencyConfigService currencyConfigService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="currencyConfig";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add";
    @Override
    public BaseService getBaseService() {
    	return this.currencyConfigService;
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
    private final static String DEFAULT_EXPORTNAME="币种配置";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","币种","提币开关","提币每天最大数量","提币每天最大数量开关","提币手续费","是否百分比1百分比 2不是百分比","最低提币数量","最高提币数量","充值开关","充值最小数量","充值地址","转账开关","转账手续费","转账最小数量","转账最大数量"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","currencyName","extractSwitchStr","extractMaxDay","extractMaxDaySwitchStr","extractRate","percentage","extractMin","extractMax","rechargeSwitchStr","rechargeMinQuantity","rechargeAddress","transferSwitchStr","transferRate","transferMin","transferMax"};
        return columns;
    }

   @Autowired
   private CurrencyService currencyService;
    /*******自己代码**********************************************************************************************************/
    @Override
    protected void toUpdateHandle(CurrencyConfigModelDto config, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
       List<SelectDto> switchList= SwitchEnum.getList();
       modelMap.put("switchList",switchList);
        List<CurrencyModelDto> all = currencyService.getAll();
        modelMap.put("currencyList",all);
        modelMap.put("walletTypeList", WalletTypeEnum.getList());
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
