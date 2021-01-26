package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.CtcConfigModel;
import com.liuqi.business.model.CtcConfigModelDto;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.service.CtcConfigService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/ctcConfig")
public class AdminCtcConfigController extends BaseAdminController<CtcConfigModel,CtcConfigModelDto> {

    @Autowired
    private CtcConfigService ctcConfigService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="ctcConfig";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.ctcConfigService;
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
    private final static String DEFAULT_EXPORTNAME="CTC配置";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","币种id","买开关0关闭 1开启","卖开关0关闭 1开启","买最小数量","卖最小数量","买最大数量","卖最大数量","价格","zb查询价格字段","买价格涨幅","卖价格涨幅"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","currencyName","buySwitchStr","sellSwitchStr","buyMin","sellMin","buyMax","sellMax","price","outerPrice","buyRang","sellRang"};
        return columns;
    }

    @Override
    protected boolean addLogger() {
        return true;
    }

    @Override
    protected boolean updateLogger() {
        return true;
    }

    /*******自己代码**********************************************************************************************************/

    @Autowired
    private CurrencyService currencyService;

    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    @Override
    protected void toAddHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    @Override
    protected void toUpdateHandle(CtcConfigModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap) {
        List<CurrencyModelDto> list = currencyService.getAll();
        modelMap.put("currencyList", list);
        List<SelectDto> switchList = SwitchEnum.getList();
        modelMap.put("switchList", switchList);
    }

    @Override
    protected void addHandle(CtcConfigModelDto t, Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.addHandle(t, curUserId, modelMap, request, response);
        //判断是否存在
        CtcConfigModelDto configTemp=ctcConfigService.getByCurrencyId(t.getCurrencyId());
        if(configTemp!=null){
            throw new BusinessException("币种配置已存在");
        }
    }

    @Override
    protected void updateHandle(CtcConfigModelDto t, Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.updateHandle(t, curUserId, modelMap, request, response);
    }
}
