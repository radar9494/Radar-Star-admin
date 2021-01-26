package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.base.LoginAdminUserHelper;
import com.liuqi.business.model.OtcConfigModelDto;
import com.liuqi.business.model.OtcWalletModel;
import com.liuqi.business.model.OtcWalletModelDto;
import com.liuqi.business.model.UserWalletModelDto;
import com.liuqi.business.service.OtcConfigService;
import com.liuqi.business.service.OtcWalletService;
import com.liuqi.business.service.UserService;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import com.liuqi.business.dto.SelectDto;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/otcWallet")
public class AdminOtcWalletController extends BaseAdminController<OtcWalletModel,OtcWalletModelDto> {

    @Autowired
    private OtcWalletService otcWalletService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="otcWallet";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.otcWalletService;
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
    private final static String DEFAULT_EXPORTNAME="otc钱包";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","币种id","用户","可用","冻结"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","currencyId","userId","using","freeze"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/


    @RequestMapping("/toShow")
    public String toShow(ModelMap modelMap){

        return JSP_BASE_PTH+"/"+BASE_MODUEL+"/otcWalletShow";
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
    protected void toUpdateHandle(OtcWalletModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(OtcWalletModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void listHandle(OtcWalletModelDto searchDto, HttpServletRequest request) {
        super.listHandle(searchDto, request);
        if(StringUtils.isNotEmpty(searchDto.getUserName())){
            searchDto.setUserId(userService.queryIdByName(searchDto.getUserName()));
        }
    }

    @Autowired
    private OtcConfigService otcConfigService;
    @Autowired
    private UserService userService;

    private void getEnumList(ModelMap modelMap){
        List<OtcConfigModelDto> list = otcConfigService.queryListByDto(null, true);
        modelMap.put("currencyList",list);
    }


    @RequestMapping("/updateP")
    @ResponseBody
    public ReturnResponse updateP(OtcWalletModel model, HttpServletRequest request) {
        Long userId = LoginAdminUserHelper.getAdminId();
        if(model.getFreeze().compareTo(BigDecimal.ZERO)<0){
            return ReturnResponse.backFail("冻结币不能输入小于0的数");
        }
        otcWalletService.adminUpdate(model,userId);
        return ReturnResponse.backSuccess();
    }
}
