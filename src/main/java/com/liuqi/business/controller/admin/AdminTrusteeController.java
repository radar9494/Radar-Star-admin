package com.liuqi.business.controller.admin;


import com.alibaba.fastjson.JSONObject;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.base.LoginAdminUserHelper;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.TrusteeStatusEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.CurrencyAreaService;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.business.service.TrusteeService;
import com.liuqi.business.service.UserService;
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
import java.util.List;

@Controller
@RequestMapping("/admin/trustee")
public class AdminTrusteeController extends BaseAdminController<TrusteeModel,TrusteeModelDto> {

    @Autowired
    private TrusteeService trusteeService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="trustee";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.trusteeService;
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
    private final static String DEFAULT_EXPORTNAME="交易";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","发布人","币种交易id","交易类型","数量","挂单价格","已交易数量","状态0未交易  1交易完毕 2取消交易 3异常","优先级","手续费比率","白名单1是 2不是","机器人1是 2不是"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","userName","tradeId","tradeTypeStr","quantity","price","tradeQuantity","statusStr","priority","rate","whiteStr","robotStr"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private CurrencyAreaService currencyAreaService;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private UserService userService;
    @Override
    protected void listHandle(TrusteeModelDto dto, HttpServletRequest request) {
        //设置用户
        super.listHandle(dto, request);
        //设置用户
        String userName=dto.getUserName();
        if(StringUtils.isNotEmpty(userName)){
            Long userId=userService.queryIdByName(userName);
            dto.setUserId(userId);
        }
    }
    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<CurrencyTradeModelDto> tradeList = currencyTradeService.queryListByDto(new CurrencyTradeModelDto(),true);
        modelMap.put("tradeList",tradeList);

        List<SelectDto> tradetypeList = BuySellEnum.getList();
        modelMap.put("tradetypeList",tradetypeList);

        List<SelectDto> statusList = TrusteeStatusEnum.getList();
        modelMap.put("statusList",statusList);

    }
    @ResponseBody
    @RequestMapping("/cancel")
    public ReturnResponse cancel(@RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request) {
        Long adminId = LoginAdminUserHelper.getAdminId();
        //日志
        BaseAdminController.opeLogger.set(getLogger(adminId, LoggerModelDto.TYPE_UPDATE,"修改", "取消订单"+id));

        trusteeService.cancel(id,0L,false);
        return ReturnResponse.builder().code(ReturnResponse.RETURN_OK).msg("操作成功").build();
    }
    @ResponseBody
    @RequestMapping("/errorModify")
    public ReturnResponse errorModify( ModelMap modelMap, HttpServletRequest request) {
        Long adminId = LoginAdminUserHelper.getAdminId();
        //日志
        BaseAdminController.opeLogger.set(getLogger(adminId, LoggerModelDto.TYPE_UPDATE,"修改", "修改异常未正常状态"));

        trusteeService.errorModify();
        return ReturnResponse.builder().code(ReturnResponse.RETURN_OK).msg("操作成功").build();
    }

    @ResponseBody
    @RequestMapping("/cancelTrade")
    public ReturnResponse cancelTrade(@RequestParam("tradeId") Long tradeId, ModelMap modelMap, HttpServletRequest request) {
        Long adminId = LoginAdminUserHelper.getAdminId();
        TrusteeModelDto search=new TrusteeModelDto();
        search.setTradeId(tradeId);
        search.setStatus(TrusteeStatusEnum.WAIT.getCode());
        List<TrusteeModelDto> list = trusteeService.queryListByDto(search,false);
        if(list!=null && list.size()>0){
            for(TrusteeModelDto dto:list){
                trusteeService.cancel(dto.getId(),0L,false);
            }
        }
        return ReturnResponse.backSuccess();
    }
}
