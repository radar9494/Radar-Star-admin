package com.liuqi.business.controller.admin;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.SuperNodeChargeModel;
import com.liuqi.business.model.SuperNodeChargeModelDto;
import com.liuqi.business.model.SuperNodeConfigModel;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.SuperNodeChargeService;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Controller
@RequestMapping("/admin/superNodeCharge")
public class AdminSuperNodeChargeController extends BaseAdminController<SuperNodeChargeModel,SuperNodeChargeModelDto> {

    @Autowired
    private SuperNodeChargeService superNodeChargeService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="superNodeCharge";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add,update";
    @Override
    public BaseService getBaseService() {
    	return this.superNodeChargeService;
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
    private final static String DEFAULT_EXPORTNAME="超级节点分红总额";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","统计时间","币种","数量","币种价格备份"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","startDate","currencyName","quantity","snapPrice"};
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

    private void getEnumList(ModelMap modelMap) {
        modelMap.put("list", currencyService.getAll());
    }


    /**
     * 手续费
     *
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/totalCharge")
    @ResponseBody
    public ReturnResponse totalCharge(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        Date date = DateTime.now().offset(DateField.DAY_OF_YEAR, -1);
        superNodeChargeService.totalCharge(date);
        return ReturnResponse.backSuccess();
    }
}
