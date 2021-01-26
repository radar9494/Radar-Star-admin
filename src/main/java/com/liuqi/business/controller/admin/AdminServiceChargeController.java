package com.liuqi.business.controller.admin;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.ServiceChargeModel;
import com.liuqi.business.model.ServiceChargeModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.ServiceChargeService;
import com.liuqi.response.ReturnResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/serviceCharge")
public class AdminServiceChargeController extends BaseAdminController<ServiceChargeModel, ServiceChargeModelDto> {

    @Autowired
    private ServiceChargeService serviceChargeService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "admin";
    //模块
    private final static String BASE_MODUEL = "serviceCharge";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "add,update";

    @Override
    public BaseService getBaseService() {
        return this.serviceChargeService;
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
    private final static String DEFAULT_EXPORTNAME = "手续费";

    @Override
    public String[] getDefaultExportHeaders() {
        String[] headers = {"主键", "创建时间", "更新时间", "备注", "版本号", "统计日期", "币种id", "手续费"};
        return headers;
    }

    @Override
    public String[] getDefaultExportColumns() {
        String[] columns = {"id", "createTime", "updateTime", "remark", "version", "calcDate", "currencyName", "charge"};
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


    /**
     * 手动汇总
     *
     * @param remark
     * @param request
     * @return
     */
    @RequestMapping(value = "/totalRecharge")
    @ResponseBody
    public ReturnResponse totalRecharge(String remark, HttpServletRequest request) {
        try {
            Date date = DateTime.now().offset(DateField.DAY_OF_YEAR, -1);
            serviceChargeService.totalCharge(date);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }
    }
}
