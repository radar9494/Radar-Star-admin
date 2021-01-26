package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.ApiTransferTypeEnum;
import com.liuqi.business.enums.WalletDoEnum;
import com.liuqi.business.model.ApiTransferModel;
import com.liuqi.business.model.ApiTransferModelDto;
import com.liuqi.business.service.ApiTransferService;
import com.liuqi.business.service.CurrencyService;
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

@Controller
@RequestMapping("/admin/apiTransfer")
public class AdminApiTransferController extends BaseAdminController<ApiTransferModel,ApiTransferModelDto> {

    @Autowired
    private ApiTransferService apiTransferService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="apiTransfer";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
        return this.apiTransferService;
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
    private final static String DEFAULT_EXPORTNAME="外盘转入";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","用户id","币种","数量","0未审核 1审核通过 2拒绝","类型0可用 1锁仓","转入名称","外部编号唯一码","外部转入人"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","userId","currencyId","quantity","status","type","name","num","transfername"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/

    @Autowired
    private UserService userService;
    @Autowired
    private CurrencyService currencyService;
    @Override
    protected void listHandle(ApiTransferModelDto dto, HttpServletRequest request) {
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

    private void getEnumList(ModelMap modelMap){
        List<SelectDto> typeList= ApiTransferTypeEnum.getList();
        modelMap.put("typeList",typeList);
        modelMap.put("currencyList",currencyService.getAll());
        modelMap.put("statusList",WalletDoEnum.getList());
    }

    /**
     * 手动处理订单
     *
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "/todo")
    public String todo(@RequestParam("id") Long id, HttpServletRequest request, ModelMap modelMap) {
        ApiTransferModel transfer = apiTransferService.getById(id);
        modelMap.put("m", transfer);
        return getJspBasePath() + "/" + getBaseModuel() + "/" + getBaseModuel() + "View";
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
    public ReturnResponse confirm(@RequestParam("id") Long id, @RequestParam(value = "reason", required = false) String reason,
                                  @RequestParam(value = "hash", required = false) String hash, HttpServletRequest request) {
        ApiTransferModel transfer = apiTransferService.getById(id);
        if (transfer == null) {
            return ReturnResponse.backFail("订单不存在");
        }
        if (WalletDoEnum.SUCCESS.getCode().equals(transfer.getStatus())) {
            return ReturnResponse.backFail("订单已处理");
        }
        String key = LockConstant.LOCK_API_TRANSFER + id;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            apiTransferService.agree(id, reason);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail("订单处理失败" + e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
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
    public ReturnResponse refuse(@RequestParam("id") Long id, @RequestParam(value = "reason", required = false) String reason, HttpServletRequest request) {
        ApiTransferModel transfer = apiTransferService.getById(id);
        if (transfer == null) {
            return ReturnResponse.backFail("订单不存在");
        }
        if (WalletDoEnum.SUCCESS.getCode().equals(transfer.getStatus())) {
            return ReturnResponse.backFail("订单已处理");
        }
        String key = LockConstant.LOCK_API_TRANSFER + id;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            apiTransferService.agree(id, reason);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail("订单处理失败" + e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }
}
