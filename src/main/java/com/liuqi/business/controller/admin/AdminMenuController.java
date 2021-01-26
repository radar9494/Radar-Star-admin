package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.MenuModel;
import com.liuqi.business.model.MenuModelDto;
import com.liuqi.business.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/menu")
public class AdminMenuController extends BaseAdminController<MenuModel, MenuModelDto> {

    @Autowired
    private MenuService menuService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="menu";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.menuService;
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
    private final static String DEFAULT_EXPORTNAME="菜单";
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
    @Override
    public void toAddHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<MenuModelDto> menuList = menuService.findFirstLevel();
        modelMap.put("menus",menuList);
    }

    @Override
    public void toUpdateHandle(MenuModelDto menu, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<MenuModelDto> menuList = menuService.findFirstLevel();
        modelMap.put("menus",menuList);
    }
}
