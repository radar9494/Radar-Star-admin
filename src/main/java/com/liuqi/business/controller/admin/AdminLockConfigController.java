package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.model.LockConfigModel;
import com.liuqi.business.model.LockConfigModelDto;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.business.service.LockConfigService;
import com.liuqi.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/lockConfig")
public class AdminLockConfigController extends BaseAdminController<LockConfigModel,LockConfigModelDto> {

    @Autowired
    private LockConfigService lockConfigService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="lockConfig";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.lockConfigService;
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
    private final static String DEFAULT_EXPORTNAME="锁仓配置";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","锁仓币种","释放交易对","释放开始时间","释放结束时间","买释放开关0关 1开","卖释放开关0关 1开","每日最大释放次数","每次释放百分比%","每天释放总百分比%","每天释放最大值"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id", "createTime", "updateTime", "remark", "version", "currencyName", "tradeName", "startTime", "endTime", "buySwitchStr", "sellSwitchStr", "times", "timesRate", "dayRate", "dayMax"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private CurrencyTradeService currencyTradeService;

    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toListHandle(modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toUpdateHandle(LockConfigModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void addHandle(LockConfigModelDto t, Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.addHandle(t, curUserId, modelMap, request, response);
        LockConfigModel temp = lockConfigService.getByTradeId(t.getTradeId());
        if (temp != null) {
            throw new BusinessException("交易对已存在");
        }
        temp = lockConfigService.getByCurrencyId(t.getCurrencyId());
        if (temp != null) {
            throw new BusinessException("币种已存在");
        }
    }

    @Override
    protected void toAddHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toAddHandle(modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap) {
        List<CurrencyTradeModelDto> tradeList = currencyTradeService.queryListByDto(new CurrencyTradeModelDto(), true);
        modelMap.put("tradeList", tradeList);
        modelMap.put("switchList", SwitchEnum.getList());
    }
}
