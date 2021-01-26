package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.base.LoginAdminUserHelper;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.OtcOrderStatusEnum;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.OtcOrderModel;
import com.liuqi.business.model.OtcOrderModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.OtcOrderService;
import com.liuqi.business.service.UserService;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/otcOrder")
public class AdminOtcOrderController extends BaseAdminController<OtcOrderModel,OtcOrderModelDto> {

    @Autowired
    private OtcOrderService otcOrderService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="otcOrder";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add,update";
    @Override
    public BaseService getBaseService() {
    	return this.otcOrderService;
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
    private final static String DEFAULT_EXPORTNAME="OTC订单";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","币种id","用户id","类型0买 1卖","价格","发布数量","已交易数量","最小金额","最大金额","银行卡0不支持 1支持","支付宝0不支持 1支持","微信0不支持 1支持","状态（0待交易，1交易中，2交易结束）"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","currencyName","userName","typeStr","price","quantity","tradeQuantity","min","max","yhk","zfb","wx","statusStr"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserService userService;

    @Override
    protected void listHandle(OtcOrderModelDto dto, HttpServletRequest request) {
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
        super.toListHandle(modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toAddHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toAddHandle(modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toUpdateHandle(OtcOrderModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(OtcOrderModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
        List<SelectDto> statusList= OtcOrderStatusEnum.getList();
        modelMap.put("statusList",statusList);

        List<CurrencyModelDto> list = currencyService.getAll();
        modelMap.put("currencyList", list);

        List<SelectDto> typeList= BuySellEnum.getList();
        modelMap.put("typeList",typeList);

    }


    @RequestMapping("/cancel")
    @ResponseBody
    public ReturnResponse add(@RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        Long adminId = LoginAdminUserHelper.getAdminId();
        String key = LockConstant.LOCK_OTC_ORDER + id;
        RLock lock=null;
        try {
            lock = RedissonLockUtil.lock(key);
            otcOrderService.cancel(id, 0L, false);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }
}
