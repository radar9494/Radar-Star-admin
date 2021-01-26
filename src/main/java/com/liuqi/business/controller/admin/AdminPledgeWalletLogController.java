package com.liuqi.business.controller.admin;


import com.google.common.base.Strings;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.PledgeWalletLogModel;
import com.liuqi.business.model.PledgeWalletLogModelDto;
import com.liuqi.business.model.UserModelDto;
import com.liuqi.business.service.PledgeWalletLogService;
import com.liuqi.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.PledgeWalletLogTypeEnum;

@Controller
@RequestMapping("/admin/pledgeWalletLog")
public class AdminPledgeWalletLogController extends BaseAdminController<PledgeWalletLogModel,PledgeWalletLogModelDto> {

    @Autowired
    private PledgeWalletLogService pledgeWalletLogService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="pledgeWalletLog";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.pledgeWalletLogService;
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
    private final static String DEFAULT_EXPORTNAME="1";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","用户id","类型","金额 ","余额 "};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","userId","type","money","balance"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/



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
    protected void toUpdateHandle(PledgeWalletLogModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(PledgeWalletLogModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
        List<SelectDto> typeList= PledgeWalletLogTypeEnum.getList();
        modelMap.put("typeList",typeList);

    }

    @Autowired
    private UserService userService;

    @Override
    protected void listHandle(PledgeWalletLogModelDto searchDto, HttpServletRequest request) {
        super.listHandle(searchDto, request);

        if (!Strings.isNullOrEmpty(searchDto.getUserName())){

            UserModelDto userModelDto = userService.queryByName(searchDto.getUserName());
            Assert.notNull(userModelDto,"用户不存在");
            searchDto.setUserId(userModelDto.getId());
        }
    }
}
