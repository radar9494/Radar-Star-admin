package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.OtcConfigModel;
import com.liuqi.business.model.OtcConfigModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.OtcConfigService;
import com.liuqi.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/otcConfig")
public class AdminOtcConfigController extends BaseAdminController<OtcConfigModel,OtcConfigModelDto> {

    @Autowired
    private OtcConfigService otcConfigService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="otcConfig";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.otcConfigService;
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
    private final static String DEFAULT_EXPORTNAME="OTC陪住";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","币种id","买开关0关闭 1开启","卖开关0关闭 1开启"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","currencyName","buySwitchStr","sellSwitchStr"};
        return columns;
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
    protected void toUpdateHandle(OtcConfigModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
        List<CurrencyModelDto> list= currencyService.getAll();
        modelMap.put("currencyList",list);
        List<SelectDto> switchList= SwitchEnum.getList();
        modelMap.put("switchList",switchList);
    }


    @Override
    protected void addHandle(OtcConfigModelDto t, Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.addHandle(t, curUserId, modelMap, request, response);
        //判断是否存在
        OtcConfigModelDto configTemp=otcConfigService.getByCurrencyId(t.getCurrencyId());
        if(configTemp!=null){
            throw new BusinessException("币种配置已存在");
        }
    }

    @Override
    protected void updateHandle(OtcConfigModelDto t, Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.updateHandle(t, curUserId, modelMap, request, response);
        //判断是否存在
        OtcConfigModelDto configTemp=otcConfigService.getByCurrencyId(t.getCurrencyId());
        if(configTemp!=null && !configTemp.getId().equals(t.getId())){
            throw new BusinessException("币种配置已存在");
        }
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
