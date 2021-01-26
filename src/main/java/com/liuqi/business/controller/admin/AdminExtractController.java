package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.base.LoginAdminUserHelper;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.dto.chain.ExtractSearchDto;
import com.liuqi.business.enums.ExtractMoneyEnum;
import com.liuqi.business.enums.InnerOuterEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.AutoExtractService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.ExtractService;
import com.liuqi.business.service.UserService;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.response.DataResult;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.MethodLimit;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/admin/extract")
public class AdminExtractController extends BaseAdminController<ExtractModel,ExtractModelDto> {

    @Autowired
    private ExtractService extractService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="extract";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add,update";
    @Override
    public BaseService getBaseService() {
    	return this.extractService;
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
    private final static String DEFAULT_EXPORTNAME="提现";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"用户", "币种", "数量", "地址", "实际数量", "手续费", "状态", "提现hash"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"userName", "currencyName", "quantity", "address", "realQuantity", "rate", "statusStr", "hash"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserService userService;
    @Autowired
    private AutoExtractService autoExtractService;
    @Autowired
    private MethodLimit methodLimit;
    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<CurrencyModelDto> list= currencyService.getAll();
        modelMap.put("list",list);
        List<SelectDto> statusList= ExtractMoneyEnum.getList();
        modelMap.put("statusList",statusList);
        List<SelectDto> typeList= InnerOuterEnum.getList();
        modelMap.put("typeList",typeList);
    }

    @Override
    protected void listHandle(ExtractModelDto dto, HttpServletRequest request) {
        //设置用户
        super.listHandle(dto, request);
        //设置用户
        String userName=dto.getUserName();
        if(StringUtils.isNotEmpty(userName)){
            Long userId=userService.queryIdByName(userName);
            dto.setUserId(userId);
        }
    }

    /**
     * 确定提取订单
     *
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "/confirm")
    @ResponseBody
    public ReturnResponse confirmRechargeOrder(@RequestParam("id") Long id,@RequestParam(value = "reason",required = false)String reason,
                                               @RequestParam(value = "hash",required = false)String hash, HttpServletRequest request) {
        ExtractModel extracrecord = extractService.getById(id);
        if (extracrecord == null) {
            return ReturnResponse.backFail("订单不存在");
        }
        if (!ExtractMoneyEnum.APPLY_ING.getCode().equals(extracrecord.getStatus())) {
            return ReturnResponse.backFail("订单已处理");
        }
        String key= LockConstant.LOCK_EXTRACT_ORDER+id;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            Long adminId = LoginAdminUserHelper.getAdminId();
            extractService.confirmOrder(id, reason,hash,adminId);
            return ReturnResponse.backSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return ReturnResponse.backFail("订单处理失败"+e.getMessage());
        }finally {
            RedissonLockUtil.unlock(lock);
        }
    }







    @RequestMapping(value = "/confirms")
    @ResponseBody
    public ReturnResponse confirmRechargeOrder(String ids, int type, HttpServletRequest request) {
        String []idss=ids.split(",");
        for(String s:idss) {
            Long id=Long.parseLong(s);
            String key= LockConstant.LOCK_EXTRACT_ORDER+id;
            RLock lock = null;
            try {
                lock = RedissonLockUtil.lock(key);
                Long adminId = LoginAdminUserHelper.getAdminId();
              if(type==0){
                  extractService.confirmOrder(id, null,null,adminId);
              }else{
                  extractService.refuseOrder(id,null,adminId);
              }
            }catch (Exception e){
                e.printStackTrace();
                return ReturnResponse.backFail("订单处理失败"+e.getMessage());
            }finally {
                RedissonLockUtil.unlock(lock);
            }
        }
        return ReturnResponse.backSuccess();
    }


    @Override
    public DataResult getList(ExtractModelDto rechargeModelDto, HttpServletRequest request, int page, int limit) {
        DataResult list = super.getList(rechargeModelDto, request, page, limit);
        BigDecimal total= extractService.getTotal(rechargeModelDto);
        list.setTotal(total);
        return list;
    }








    /**
     * 拒绝提取
     *
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "/refuse")
    @ResponseBody
    public ReturnResponse refuseRechargeOrder(@RequestParam("id") Long id,@RequestParam(value = "reason",required = false)String reason, HttpServletRequest request) {
        ExtractModel extracrecord = extractService.getById(id);
        if (extracrecord == null) {
            return ReturnResponse.backFail("订单不存在");
        }
        if (!ExtractMoneyEnum.APPLY_ING.getCode().equals(extracrecord.getStatus())) {
            return ReturnResponse.backFail("订单已处理");
        }
        String key= LockConstant.LOCK_EXTRACT_ORDER+id;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            Long adminId = LoginAdminUserHelper.getAdminId();
            extractService.refuseOrder(id, reason,adminId);
            return ReturnResponse.backSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return ReturnResponse.backFail("订单处理失败"+e.getMessage());
        }finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    /**
     * 自动提取
     *
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "/autoExtract")
    @ResponseBody
    public ReturnResponse autoExtract(@RequestParam("id") Long id, HttpServletRequest request) {
        ExtractModel extracRecord = extractService.getById(id);
        if (extracRecord == null) {
            return ReturnResponse.backFail("订单不存在");
        }
        if (!ExtractMoneyEnum.APPLY_ING.getCode().equals(extracRecord.getStatus())) {
            return ReturnResponse.backFail("订单已处理");
        }
        Long adminId = LoginAdminUserHelper.getAdminId();
        String key= LockConstant.LOCK_EXTRACT_ORDER+id;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            extractService.autoExtract(id, adminId);
            return ReturnResponse.backSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return ReturnResponse.backFail("订单处理失败"+e.getMessage());
        }finally {
            RedissonLockUtil.unlock(lock);
        }
    }
    /**
     * 手动查询提现接口数据
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ReturnResponse queryInfo() {
        int count= methodLimit.residueTimes("extractquery","",1,1L, TimeUnit.MINUTES);
        if(count>0){
            autoExtractService.queryInfo();
        }
        return  ReturnResponse.backSuccess();
    }

    /**
     * 手动查询提现接口数据
     */
    @RequestMapping(value = "/update2Doing")
    @ResponseBody
    public ReturnResponse update2Doing(@RequestParam("id") Long id) {
        ExtractModel extracRecord = extractService.getById(id);
        if(ExtractMoneyEnum.APPLY_ERROR.getCode().equals(extracRecord.getStatus())){
            extracRecord.setStatus(ExtractMoneyEnum.APPLY_DOING.getCode());
            extracRecord.setRemark("");
            extractService.update(extracRecord);
            return ReturnResponse.backSuccess();
        }
        return ReturnResponse.backFail("非异常单不能处理");
    }


    /**
     * 手动处理订单
     *
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "/todo")
    public String todo(@RequestParam("id") Long id, HttpServletRequest request,ModelMap modelMap) {
        ExtractModelDto extractModel = extractService.getById(id);
        UserModel user=userService.getById(extractModel.getUserId());
        CurrencyModel currency=currencyService.getById(extractModel.getCurrencyId());
        extractModel.setUserName(user.getName());
        extractModel.setCurrencyName(currency.getName());
        modelMap.put("m",extractModel);
        return JSP_BASE_PTH+"/"+BASE_MODUEL+"/"+BASE_MODUEL+"View";
    }
}
