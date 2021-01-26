package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.TransferModel;
import com.liuqi.business.model.TransferModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.TransferService;
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
@RequestMapping("/admin/transfer")
public class AdminTransferController extends BaseAdminController<TransferModel,TransferModelDto> {

    @Autowired
    private TransferService transferService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="transfer";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.transferService;
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
    private final static String DEFAULT_EXPORTNAME="转账";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","用户id","接受id","币种id","手续费","数量","兑换数量"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","userId","receiveId","currencyId","rate","quantity","realQuantity"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private UserService userService;
    @Autowired
    private CurrencyService currencyService;

    @Override
    protected void listHandle(TransferModelDto searchDto, HttpServletRequest request) {
        super.listHandle(searchDto, request);
        if(StringUtils.isNotEmpty(searchDto.getName())){
            Long userId= userService.queryIdByName(searchDto.getName());
            searchDto.setUserId(userId);
        }
        if(StringUtils.isNotEmpty(searchDto.getReceiveName())){
            Long userId= userService.queryIdByName(searchDto.getReceiveName());
            searchDto.setReceiveId(userId);
        }



    }


    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<CurrencyModelDto> list=currencyService.getAll();
        modelMap.put("currencyList",list);
    }




}
