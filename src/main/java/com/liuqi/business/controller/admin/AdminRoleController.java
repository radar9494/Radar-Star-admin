package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.MenuDto;
import com.liuqi.business.model.RoleModel;
import com.liuqi.business.model.RoleModelDto;
import com.liuqi.business.service.MenuService;
import com.liuqi.business.service.RoleMenuService;
import com.liuqi.business.service.RolePermissionService;
import com.liuqi.business.service.RoleService;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/role")
public class AdminRoleController extends BaseAdminController<RoleModel, RoleModelDto> {

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;
    @Autowired
    private RoleMenuService roleMenuService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="role";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.roleService;
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
    protected String getNotOperate() {
        return NOT_OPERATE;
    }

    @Override
    public String getDefaultExportName() {
        return DEFAULT_EXPORTNAME;
    }
    /*******待修改  排序  导出**********************************************************************************************************/
    //默认导出名称
    private final static String DEFAULT_EXPORTNAME="角色";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"编码"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private RolePermissionService rolePermissionService;
    @RequestMapping(value = "/toRelMenu")
    public String toRelMenu(Long id, ModelMap modelMap) throws Exception{
        //获取权限
        RoleModel role = roleService.getById(id);
        List<MenuDto> array=menuService.getAllMenuAndSelect(id);
        List<Long> plist=rolePermissionService.getPermissionIdByRole(role.getId());
        modelMap.put("m", role);
        modelMap.put("array", array);
        modelMap.put("plist", plist);
        return getJspBasePath() + "/" + getBaseModuel() +"/roleMenu";
    }

    @RequestMapping(value = "/menuRel")
    @ResponseBody
    public ReturnResponse relMenu(Long roleId, String menus, String permissions){
        if (roleId > 0 ) {
            try {
                List<Long> menuList=new ArrayList<Long>();
                if(StringUtils.isNotEmpty(menus)) {
                    for (String s : menus.split(",")) {
                        menuList.add(Long.valueOf(s));
                    }
                }
                List<Long> permissionList=new ArrayList<Long>();
                if(StringUtils.isNotEmpty(permissions)) {
                    for (String s : permissions.split(",")) {
                        permissionList.add(Long.valueOf(s));
                    }
                }
                roleMenuService.rel(roleId,menuList,permissionList);
                return ReturnResponse.backSuccess();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ReturnResponse.backFail("设置失败");
    }
}
