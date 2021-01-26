package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.UserStoreStatusEnum;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.model.UserStoreModel;
import com.liuqi.business.model.UserStoreModelDto;
import com.liuqi.business.service.UserService;
import com.liuqi.business.service.UserStoreService;
import com.liuqi.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/userStore")
public class AdminUserStoreController extends BaseAdminController<UserStoreModel, UserStoreModelDto> {

    @Autowired
    private UserStoreService userStoreService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "admin";
    //模块
    private final static String BASE_MODUEL = "userStore";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "";

    @Override
    public BaseService getBaseService() {
        return this.userStoreService;
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
    private final static String DEFAULT_EXPORTNAME = "CTC承运商";

    @Override
    public String[] getDefaultExportHeaders() {
        String[] headers = {"主键", "创建时间", "更新时间", "备注", "版本号", "用户id", "状态 0未启用 1启用", "姓名", "卡号", "地址", "银行名称"};
        return headers;
    }

    @Override
    public String[] getDefaultExportColumns() {
        String[] columns = {"id", "createTime", "updateTime", "remark", "version", "userId", "status", "name", "bankNo", "bankAddress", "bankName"};
        return columns;
    }

    /*******自己代码**********************************************************************************************************/

    @Autowired
    private UserService userService;

    @Override
    protected void listHandle(UserStoreModelDto dto, HttpServletRequest request) {
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
    protected void addHandle(UserStoreModelDto t, Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.updateHandle(t, curUserId, modelMap, request, response);
        UserModel temp=userService.queryByName(t.getUserName());
        if(temp==null){
            throw new BusinessException("用户不存在");
        }
        t.setUserId(temp.getId());
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
    protected void toUpdateHandle(UserStoreModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap) {
        List<SelectDto> statusList = UserStoreStatusEnum.getList();
        modelMap.put("statusList", statusList);

    }
}
