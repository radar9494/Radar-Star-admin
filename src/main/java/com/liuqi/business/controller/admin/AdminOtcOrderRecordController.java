package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.base.LoginAdminUserHelper;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.OtcOrderRecordStatusEnum;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.OtcOrderRecordLogModelDto;
import com.liuqi.business.model.OtcOrderRecordModel;
import com.liuqi.business.model.OtcOrderRecordModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.OtcOrderRecordLogService;
import com.liuqi.business.service.OtcOrderRecordService;
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
@RequestMapping("/admin/otcOrderRecord")
public class AdminOtcOrderRecordController extends BaseAdminController<OtcOrderRecordModel,OtcOrderRecordModelDto> {

    @Autowired
    private OtcOrderRecordService otcOrderRecordService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="otcOrderRecord";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add,update";
    @Override
    public BaseService getBaseService() {
    	return this.otcOrderRecordService;
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
    private final static String DEFAULT_EXPORTNAME="OTC记录";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","订单id","币种id","买用户id","卖用户id","类型0买 1卖","价格","数量","总金额","状态（0待接单，1待支付，2待收款，3完成 4取消 5申诉 6申诉成功 7申诉失败）","打款唯一码","申诉方向0买 1卖","申诉人","申诉内容"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","orderId","currencyName","buyUserName","sellUserName","typeStr","price","quantity","money","statusStr","memo","appealType","appealUser","appealContent"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserService userService;
    @Autowired
    private OtcOrderRecordLogService otcOrderRecordLogService;
    @Override
    protected void listHandle(OtcOrderRecordModelDto dto, HttpServletRequest request) {
        super.listHandle(dto, request);
        //设置用户
        String buyUserName=dto.getBuyUserName();
        if(StringUtils.isNotEmpty(buyUserName)){
            Long userId=userService.queryIdByName(buyUserName);
            dto.setBuyUserId(userId);
        }

        String sellUserName=dto.getSellUserName();
        if(StringUtils.isNotEmpty(sellUserName)){
            Long userId=userService.queryIdByName(sellUserName);
            dto.setSellUserId(userId);
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
    protected void toUpdateHandle(OtcOrderRecordModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }



    private void getEnumList(ModelMap modelMap){
        List<SelectDto> statusList= OtcOrderRecordStatusEnum.getList();
        modelMap.put("statusList",statusList);

        List<CurrencyModelDto> list = currencyService.getAll();
        modelMap.put("currencyList", list);

        List<SelectDto> typeList= BuySellEnum.getList();
        modelMap.put("typeList",typeList);

    }

    @Override
    protected void toViewHandle(OtcOrderRecordModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        List<OtcOrderRecordLogModelDto> list= otcOrderRecordLogService.getByRecordId(t.getId());
        modelMap.put("list",list);
    }


    /**
     * 申诉
     * @param id
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/toAppeal")
    public String toAppeal(@RequestParam("id")Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        OtcOrderRecordModelDto record=otcOrderRecordService.getById(id);
        List<OtcOrderRecordLogModelDto> list= otcOrderRecordLogService.getByRecordId(record.getId());

        modelMap.put("record",record);
        modelMap.put("list",list);
        return getJspBasePath() + "/" + getBaseModuel() + "/appeal";
    }



    @RequestMapping("/appealSuccess")
    @ResponseBody
    public ReturnResponse appealSuccess(@RequestParam("id") Long id, @RequestParam("remark") String remark,
                                        ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        Long adminId = LoginAdminUserHelper.getAdminId();
        OtcOrderRecordModel record = otcOrderRecordService.getById(id);
        if (record == null) {
            return ReturnResponse.backFail("订单异常");
        }
        String key = LockConstant.LOCK_OTC_ORDER + record.getOrderId();
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            otcOrderRecordService.appealSuccess(adminId, id, remark);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    @RequestMapping("/appealFail")
    @ResponseBody
    public ReturnResponse appealFail(@RequestParam("id") Long id, @RequestParam("remark") String remark,
                                     ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        Long adminId = LoginAdminUserHelper.getAdminId();
        OtcOrderRecordModel record = otcOrderRecordService.getById(id);
        if (record == null) {
            return ReturnResponse.backFail("订单异常");
        }
        String key = LockConstant.LOCK_OTC_ORDER + record.getOrderId();
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            otcOrderRecordService.appealFail(adminId, id, remark);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }
}
