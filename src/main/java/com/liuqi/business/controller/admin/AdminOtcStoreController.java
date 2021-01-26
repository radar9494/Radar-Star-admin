package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.OtcStoreTypeEnum;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.OtcStoreModel;
import com.liuqi.business.model.OtcStoreModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.OtcStoreService;
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
@RequestMapping("/admin/otcStore")
public class AdminOtcStoreController extends BaseAdminController<OtcStoreModel,OtcStoreModelDto> {

    @Autowired
    private OtcStoreService otcStoreService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="otcStore";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.otcStoreService;
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
    private final static String DEFAULT_EXPORTNAME="OTC商户";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","币种id","用户id","类型（0普通，1高级）","总数量","成功数量"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","currencyName","userName","typeStr","total","success"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserService userService;

    @Override
    protected void listHandle(OtcStoreModelDto dto, HttpServletRequest request) {
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
        super.toListHandle(modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap) {
        List<CurrencyModelDto> list = currencyService.getAll();
        modelMap.put("currencyList", list);
        modelMap.put("typeList", OtcStoreTypeEnum.getList());

    }
}
