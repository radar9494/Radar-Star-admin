package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.CurrencyAreaModelDto;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.CurrencyTradeModel;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.service.CurrencyAreaService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/currencyTrade")
public class AdminCurrencyTradeController extends BaseAdminController<CurrencyTradeModel, CurrencyTradeModelDto> {

    @Autowired
    private CurrencyTradeService currencyTradeService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="currencyTrade";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.currencyTradeService;
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
    private final static String DEFAULT_EXPORTNAME="币种交易对";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","币种名称","交易币种","交易区域","显示位置","1主创区/2原创区","接口查询名称","状态","交易开关","价格控制开关","数量控制开关","涨跌幅开关","买手续费","卖手续费","最小交易价格","最大交易价格","最小交易数量","最大交易数量","涨跌幅百分比","交易输入位数"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","currencyId","tradeCurrencyId","areaId","position","area","searchName","statusStr","tradeSwitchStr","priceSwitchStr","quantitySwitchStr","limitSwitchStr","buyRate","sellRate","minPirce","maxPirce","minQuantity","maxQuantity","limitRate","digits"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private CurrencyAreaService currencyAreaService;
    @Autowired
    private CurrencyService currencyService;


    @Override
    protected void addHandle(CurrencyTradeModelDto t, Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        if(t.getCurrencyId().equals(t.getTradeCurrencyId())){
            throw new BusinessException("交易币种和被交易币种相同");
        }
        CurrencyTradeModelDto temp=currencyTradeService.getByCurrencyId(t.getCurrencyId(),t.getTradeCurrencyId());
        if(temp!=null){
            throw new BusinessException("交易对已存在");
        }
    }

    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    @Override
    protected void toAddHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }

    @Override
    protected String getNotIncludeField() {
        return ",id,createTime,updateTime,version,currencyId,tradeCurrencyId,areaId,";
    }

    @Override
    protected void toUpdateHandle(CurrencyTradeModelDto currencyTradeModel, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        this.getEnumList(modelMap);
    }
    @Override
    protected boolean addLogger() {
        return true;
    }
    @Override
    protected boolean updateLogger() {
        return true;
    }

    private void getEnumList(ModelMap modelMap) {
        List<SelectDto> switchList = SwitchEnum.getList();
        modelMap.put("switchList", switchList);

        List<CurrencyAreaModelDto> areaList = currencyAreaService.findAllArea();
        modelMap.put("areaList", areaList);

        List<CurrencyModelDto> currencyList = currencyService.getAll();
        modelMap.put("currencyList", currencyList);
    }
}
