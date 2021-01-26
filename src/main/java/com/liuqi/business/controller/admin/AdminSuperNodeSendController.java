package com.liuqi.business.controller.admin;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.WalletDoEnum;
import com.liuqi.business.model.SuperNodeSendModel;
import com.liuqi.business.model.SuperNodeSendModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.SuperNodeSendService;
import com.liuqi.business.service.UserService;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Controller
@RequestMapping("/admin/superNodeSend")
public class AdminSuperNodeSendController extends BaseAdminController<SuperNodeSendModel,SuperNodeSendModelDto> {

    @Autowired
    private SuperNodeSendService superNodeSendService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="superNodeSend";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add,update";
    @Override
    public BaseService getBaseService() {
    	return this.superNodeSendService;
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
    private final static String DEFAULT_EXPORTNAME="分红";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","发放时间","用户id","币种","数量","0未发放 1已发放 2异常"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","sendDate","userName","currencyName","quantity","status"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private UserService userService;
    @Autowired
    private CurrencyService currencyService;

    @Override
    protected void listHandle(SuperNodeSendModelDto dto, HttpServletRequest request) {
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
        modelMap.put("list", currencyService.getAll());
        modelMap.put("statusList", WalletDoEnum.getList());
    }


    /**
     * 释放记录
     *
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/realseOrder")
    @ResponseBody
    public ReturnResponse realseOrder(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        Date date = DateTime.now().offset(DateField.DAY_OF_YEAR, -1);
        superNodeSendService.createChargeOrder(date);
        return ReturnResponse.backSuccess();
    }


    /**
     * 发放
     *
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/sendWallet")
    @ResponseBody
    public ReturnResponse sendWallet(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        superNodeSendService.realse();
        return ReturnResponse.backSuccess();
    }
}
