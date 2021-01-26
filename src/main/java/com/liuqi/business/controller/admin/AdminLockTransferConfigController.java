package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.LockTransferConfigModel;
import com.liuqi.business.model.LockTransferConfigModelDto;
import com.liuqi.business.service.LockConfigService;
import com.liuqi.business.service.LockTransferConfigService;
import com.liuqi.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/lockTransferConfig")
public class AdminLockTransferConfigController extends BaseAdminController<LockTransferConfigModel, LockTransferConfigModelDto> {

    @Autowired
    private LockTransferConfigService lockTransferConfigService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "admin";
    //模块
    private final static String BASE_MODUEL = "lockTransferConfig";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "";

    @Override
    public BaseService getBaseService() {
        return this.lockTransferConfigService;
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
    private final static String DEFAULT_EXPORTNAME = "转入转出配置";

    @Override
    public String[] getDefaultExportHeaders() {
        String[] headers = {"主键", "创建时间", "更新时间", "备注", "版本号", "合约币种", "转入开关0关 1开", "杠杆倍数", "转让开关0关 1开", "转让手续费(%)", "每天转让次数"};
        return headers;
    }

    @Override
    public String[] getDefaultExportColumns() {
        String[] columns = {"id", "createTime", "updateTime", "remark", "version", "currencyId", "inputSwitch", "lever", "transferSwitch", "transferRate", "transferTimes"};
        return columns;
    }

    /*******自己代码**********************************************************************************************************/
    @Autowired
    private LockConfigService lockConfigService;

    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    @Override
    protected void toAddHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    @Override
    protected void toUpdateHandle(LockTransferConfigModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap) {
        modelMap.put("currencyList", lockConfigService.getLockCurrencyList());
        modelMap.put("switchList", SwitchEnum.getList());
    }

    @Override
    protected void addHandle(LockTransferConfigModelDto t, Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.addHandle(t, curUserId, modelMap, request, response);
        LockTransferConfigModelDto temp = lockTransferConfigService.getByCurrencyId(t.getCurrencyId());
        if (temp != null) {
            throw new BusinessException("币种已存在");
        }
    }

}
