package com.liuqi.business.controller.admin;


import cn.hutool.core.util.RandomUtil;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.ApiTransferTypeEnum;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.ApiTransferConfigModel;
import com.liuqi.business.model.ApiTransferConfigModelDto;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.service.ApiTransferConfigService;
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
@RequestMapping("/admin/apiTransferConfig")
public class AdminApiTransferConfigController extends BaseAdminController<ApiTransferConfigModel, ApiTransferConfigModelDto> {

    @Autowired
    private ApiTransferConfigService apiTransferConfigService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "admin";
    //模块
    private final static String BASE_MODUEL = "apiTransferConfig";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "";

    @Override
    public BaseService getBaseService() {
        return this.apiTransferConfigService;
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
    private final static String DEFAULT_EXPORTNAME = "外盘配置";

    @Override
    public String[] getDefaultExportHeaders() {
        String[] headers = {"主键", "创建时间", "更新时间", "备注", "版本号", "外部名称", "私钥", "开关", "开始时间", "结束时间", "每天最大数量", "最大次数", "每次最大数量", "转入币种", "类型"};
        return headers;
    }

    @Override
    public String[] getDefaultExportColumns() {
        String[] columns = {"id", "createTime", "updateTime", "remark", "version", "name", "key", "onOffStr", "startTime", "endTime", "dayMaxQuantity", "dayTimes", "timesQuantity", "currencyNames", "typeStr"};
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
    protected void toUpdateHandle(ApiTransferConfigModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap) {
        List<CurrencyModelDto> list = currencyService.getAll();
        modelMap.put("currencyList", list);
        modelMap.put("switchList", SwitchEnum.getList());
        modelMap.put("typeList", ApiTransferTypeEnum.getList());
    }

    @Override
    protected void addHandle(ApiTransferConfigModelDto t, Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.addHandle(t, curUserId, modelMap, request, response);
        //判断是否存在
        ApiTransferConfigModel configTemp = apiTransferConfigService.getByName(t.getName());
        if (configTemp != null) {
            throw new BusinessException("名称已存在");
        }
        if(!t.getCurrencyIds().startsWith(",")){
            t.setCurrencyIds(","+t.getCurrencyIds());
        }
        if(!t.getCurrencyIds().endsWith(",")){
            t.setCurrencyIds(t.getCurrencyIds()+",");
        }
        t.setKey(RandomUtil.randomUUID().replaceAll("-",""));
    }

    @Override
    protected void updateHandle(ApiTransferConfigModelDto t, Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.updateHandle(t, curUserId, modelMap, request, response);
        if(!t.getCurrencyIds().startsWith(",")){
            t.setCurrencyIds(","+t.getCurrencyIds());
        }
        if(!t.getCurrencyIds().endsWith(",")){
            t.setCurrencyIds(t.getCurrencyIds()+",");
        }
    }

    @Override
    protected String getNotIncludeField() {
        return super.getNotIncludeField()+"key,";
    }
}
