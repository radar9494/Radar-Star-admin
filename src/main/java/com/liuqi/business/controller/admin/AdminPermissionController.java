package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.PermissionModel;
import com.liuqi.business.model.PermissionModelDto;
import com.liuqi.business.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/permission")
public class AdminPermissionController extends BaseAdminController<PermissionModel,PermissionModelDto> {

    @Autowired
    private PermissionService permissionService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="permission";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.permissionService;
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
    private final static String DEFAULT_EXPORTNAME="权限";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"菜单id","权限名称","按钮方法名称"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"menuId","permissionName","methodName"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/



}
