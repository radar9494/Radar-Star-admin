package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.ChargeAwardConfigModel;
import com.liuqi.business.model.ChargeAwardConfigModelDto;
import com.liuqi.business.service.ChargeAwardConfigService;
import com.liuqi.business.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/chargeAwardConfig")
public class AdminChargeAwardConfigController extends BaseAdminController<ChargeAwardConfigModel, ChargeAwardConfigModelDto> {

    @Autowired
    private ChargeAwardConfigService chargeAwardConfigService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "admin";
    //模块
    private final static String BASE_MODUEL = "chargeAwardConfig";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "add";

    @Override
    public BaseService getBaseService() {
        return this.chargeAwardConfigService;
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
    private final static String DEFAULT_EXPORTNAME = "手续费分红配置";

    @Override
    public String[] getDefaultExportHeaders() {
        String[] headers = {"主键", "创建时间", "更新时间", "备注", "版本号", "手续费奖励开关0关 1开", "奖励币种0 交易币种  否则指定币种", "奖励层数", "奖励信息"};
        return headers;
    }

    @Override
    public String[] getDefaultExportColumns() {
        String[] columns = {"id", "createTime", "updateTime", "remark", "version", "onOff", "awardCurrency", "awardLevel", "awardInfo"};
        return columns;
    }

    /*******自己代码**********************************************************************************************************/
    @Autowired
    private CurrencyService currencyService;


    @RequestMapping("/toConfig")
    public String toAdd(ModelMap modelMap, HttpServletRequest request,
                        HttpServletResponse response) {
        ChargeAwardConfigModelDto config = chargeAwardConfigService.getConfig();
        modelMap.put("m", config);
        modelMap.put("switchList", SwitchEnum.getList());
        modelMap.put("currencyList", currencyService.getAll());
        return getJspBasePath() + "/" + getBaseModuel() + "/" + getBaseModuel() + "Update";
    }

}
