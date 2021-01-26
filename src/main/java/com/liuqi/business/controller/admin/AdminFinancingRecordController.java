package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.base.LoginAdminUserHelper;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.FinancingRecordStatusEnum;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.FinancingRecordModel;
import com.liuqi.business.model.FinancingRecordModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.FinancingRecordService;
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
@RequestMapping("/admin/financingRecord")
public class AdminFinancingRecordController extends BaseAdminController<FinancingRecordModel,FinancingRecordModelDto> {

    @Autowired
    private FinancingRecordService financingRecordService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="financingRecord";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add,update";
    @Override
    public BaseService getBaseService() {
    	return this.financingRecordService;
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
    private final static String DEFAULT_EXPORTNAME="融资融币记录";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","配置id","用户id","币种id","融资币种id","融资数量","获取币种数量","状态 0未发放 1已发放"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","configId","userId","currencyId","financingCurrencyId","quantity","grantQuantity","status"};
        return columns;
    }



    /*******自己代码**********************************************************************************************************/

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserService userService;
    @Override
    protected void listHandle(FinancingRecordModelDto dto, HttpServletRequest request) {
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
    protected void toUpdateHandle(FinancingRecordModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(FinancingRecordModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
        List<SelectDto> statusList= FinancingRecordStatusEnum.getList();
        modelMap.put("statusList",statusList);

        List<CurrencyModelDto> currencyList= currencyService.getAll();
        modelMap.put("currencyList",currencyList);
    }


    /**
     * 确定提取订单
     *
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "/send")
    @ResponseBody
    public ReturnResponse send(@RequestParam("id") Long id, HttpServletRequest request) {
        FinancingRecordModelDto record = financingRecordService.getById(id);
        if (record == null) {
            return ReturnResponse.backFail("订单不存在");
        }
        if (!FinancingRecordStatusEnum.NOTGRANT.getCode().equals(record.getStatus())) {
            return ReturnResponse.backFail("订单已处理");
        }
        String key = LockConstant.LOCK_FINANCING_RECORD_GIVE + id + ":" + record.getUserId();
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            Long adminId = LoginAdminUserHelper.getAdminId();
            financingRecordService.addToWallet(record.getId(), record.getUserId(), false, true, adminId);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail("订单处理失败" + e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }
}
