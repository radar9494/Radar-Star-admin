package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.base.LoginAdminUserHelper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.LockWalletService;
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
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/lockWallet")
public class AdminLockWalletController extends BaseAdminController<LockWalletModel, LockWalletModelDto> {

    @Autowired
    private LockWalletService lockWalletService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "admin";
    //模块
    private final static String BASE_MODUEL = "lockWallet";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "add,update";

    @Override
    public BaseService getBaseService() {
        return this.lockWalletService;
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
    public String getNotOperate() {
        return NOT_OPERATE;
    }

    @Override
    public String getDefaultExportName() {
        return DEFAULT_EXPORTNAME;
    }

    /*******待修改  排序  导出**********************************************************************************************************/
    //默认导出名称
    private final static String DEFAULT_EXPORTNAME = "锁仓钱包";

    @Override
    public String[] getDefaultExportHeaders() {
        String[] headers = {"主键", "创建时间", "更新时间", "备注", "版本号", "币种id", "用户id", "锁仓数量", "冻结"};
        return headers;
    }

    @Override
    public String[] getDefaultExportColumns() {
        String[] columns = {"id", "createTime", "updateTime", "remark", "version", "currencyName", "userName", "locking", "freeze"};
        return columns;
    }

    /*******自己代码**********************************************************************************************************/


    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserService userService;

    @Override
    protected void listHandle(LockWalletModelDto dto, HttpServletRequest request) {
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
        List<CurrencyModelDto> list = currencyService.getAll();
        modelMap.put("currencyList", list);
    }

    @Override
    protected void toUpdateHandle(LockWalletModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        String userName = userService.getById(t.getUserId()).getName();
        String currencyName = currencyService.getById(t.getCurrencyId()).getName();
        modelMap.put("userName", userName);
        modelMap.put("currencyName", currencyName);
    }

    /**
     * （后台）修改
     *
     * @param
     * @param request
     * @return
     */
    @RequestMapping("/updateP")
    @ResponseBody
    public ReturnResponse updateP(LockWalletModelDto model, HttpServletRequest request) {
        try {
            Long userId = LoginAdminUserHelper.getAdminId();
            if (model.getFreeze().compareTo(BigDecimal.ZERO) < 0) {
                return ReturnResponse.backFail("冻结币不能输入小于0的数");
            }
            lockWalletService.adminUpdate(model, userId);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnResponse.backFail("修改失败");
    }

    /**
     * 补全钱包
     *
     * @param
     * @param request
     * @return
     */
    @RequestMapping("/toInitWallet")
    public String toInitWallet(UserWalletModelDto model, HttpServletRequest request) {
        return getJspBasePath() + "/" + getBaseModuel() + "/initWallet";
    }

    /**
     * （初始化
     *
     * @param
     * @param request
     * @return
     */
    @RequestMapping("/initWallet")
    @ResponseBody
    public ReturnResponse initWallet(@RequestParam("userName") String userName, HttpServletRequest request) {
        try {
            UserModelDto dto = userService.queryByName(userName);
            if (dto == null) {
                return ReturnResponse.backFail("用户不存在");
            }
            lockWalletService.insertUserWallet(dto.getId());
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnResponse.backFail("修改失败");
    }


}
