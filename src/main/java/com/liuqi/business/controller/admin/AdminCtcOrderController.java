package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.base.LoginAdminUserHelper;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.CtcOrderStatusEnum;
import com.liuqi.business.model.CtcConfigModelDto;
import com.liuqi.business.model.CtcOrderLogModelDto;
import com.liuqi.business.model.CtcOrderModel;
import com.liuqi.business.model.CtcOrderModelDto;
import com.liuqi.business.service.*;
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

@Controller
@RequestMapping("/admin/ctcOrder")
public class AdminCtcOrderController extends BaseAdminController<CtcOrderModel,CtcOrderModelDto> {

    @Autowired
    private CtcOrderService ctcOrderService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="ctcOrder";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add,update";
    @Override
    public BaseService getBaseService() {
    	return this.ctcOrderService;
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
    private final static String DEFAULT_EXPORTNAME="CTC订单";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键", "创建时间", "更新时间", "备注", "版本号", "用户id", "类型0买 2卖", "币种id", "承运商id", "价格", "数量", "总金额", "状态（处理中，处理完成，取消）", "打款唯一码", "自动结束时间"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id", "createTime", "updateTime", "remark", "version", "userName", "tradeTypeStr", "currencyName", "storeName", "price", "quantity", "money", "statusStr", "memo", "autoEndTime"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private CtcOrderLogService ctcOrderLogService;
    @Autowired
    private UserService userService;
    @Autowired
    private CtcConfigService ctcConfigService;
    @Override
    protected void listHandle(CtcOrderModelDto dto, HttpServletRequest request) {
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
        this.getEnumList(modelMap);
    }

    @Override
    protected void toUpdateHandle(CtcOrderModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        List<CtcOrderLogModelDto> list= ctcOrderLogService.getByOrderId(t.getId());
        modelMap.put("logList",list);
    }

    @Override
    protected void toViewHandle(CtcOrderModelDto m, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(m, modelMap, request, response);
        List<CtcOrderLogModelDto> list= ctcOrderLogService.getByOrderId(m.getId());
        modelMap.put("logList",list);
    }

    private void getEnumList(ModelMap modelMap) {
        List<CtcConfigModelDto> list = ctcConfigService.getAll();
        modelMap.put("currencyList", list);
        modelMap.put("typeList", BuySellEnum.getList());
        modelMap.put("statusList", CtcOrderStatusEnum.getList());
    }

    /**
     * 匹配
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/match")
    @ResponseBody
    public ReturnResponse match(@RequestParam(value = "id") Long id) throws Exception {
        ctcOrderService.matchStore(id);
        return ReturnResponse.backSuccess();
    }

    /**
     * 取消
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cancel")
    @ResponseBody
    public ReturnResponse cancel(@RequestParam(value = "id") Long id, @RequestParam(value = "reason") String reason, HttpServletRequest request) throws Exception {
        String opeName = LoginAdminUserHelper.getAdmin().getName();
        String key = LockConstant.LOCK_CTC_ORDER + id;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            ctcOrderService.cancel(id, reason, opeName);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    /**
     * 处理
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/confirm")
    @ResponseBody
    public ReturnResponse cancel(@RequestParam(value = "id") Long id, @RequestParam(value = "reason") String reason) throws Exception {
        String opeName = LoginAdminUserHelper.getAdmin().getName();
        String key = LockConstant.LOCK_CTC_ORDER + id;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            ctcOrderService.confirm(id, reason, opeName);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

}
