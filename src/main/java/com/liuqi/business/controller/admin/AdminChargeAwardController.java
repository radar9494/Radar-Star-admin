package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.WalletDoEnum;
import com.liuqi.business.model.ChargeAwardModel;
import com.liuqi.business.model.ChargeAwardModelDto;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.service.ChargeAwardService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/chargeAward")
public class AdminChargeAwardController extends BaseAdminController<ChargeAwardModel, ChargeAwardModelDto> {

    @Autowired
    private ChargeAwardService chargeAwardService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "admin";
    //模块
    private final static String BASE_MODUEL = "chargeAward";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "add,update";

    @Override
    public BaseService getBaseService() {
        return this.chargeAwardService;
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
    private final static String DEFAULT_EXPORTNAME = "手续费分红";

    @Override
    public String[] getDefaultExportHeaders() {
        String[] headers = {"主键", "创建时间", "更新时间", "备注", "版本号", "用户id", "币种", "数量", "0未发放 1已发放 2异常", "层数", "释放订单id", "成交订单id", "快照手续币种", "快照手续费数量", "快照币种数量"};
        return headers;
    }

    @Override
    public String[] getDefaultExportColumns() {
        String[] columns = {"id", "createTime", "updateTime", "remark", "version", "userName", "currencyName", "quantity", "status", "level", "orderId", "recordId", "snapChargeCurrency", "snapCharge", "snapPrice"};
        return columns;
    }

    /*******自己代码**********************************************************************************************************/
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserService userService;

    @Override
    protected void listHandle(ChargeAwardModelDto dto, HttpServletRequest request) {
        //设置用户
        super.listHandle(dto, request);
        //设置用户
        String userName = dto.getUserName();
        if (StringUtils.isNotEmpty(userName)) {
            Long userId = userService.queryIdByName(userName);
            dto.setUserId(userId);
        }
    }

    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<CurrencyModelDto> list = currencyService.getAll();
        modelMap.put("currencyList", list);
        modelMap.put("statusList", WalletDoEnum.getList());
    }


}
