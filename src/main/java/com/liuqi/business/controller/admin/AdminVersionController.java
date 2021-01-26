package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.VersionModel;
import com.liuqi.business.model.VersionModelDto;
import com.liuqi.business.service.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/version")
public class AdminVersionController extends BaseAdminController<VersionModel, VersionModelDto> {

    @Autowired
    private VersionService versionService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "admin";
    //模块
    private final static String BASE_MODUEL = "version";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "add";

    @Override
    public BaseService getBaseService() {
        return this.versionService;
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
    private final static String DEFAULT_EXPORTNAME = "app版本";

    @Override
    public String[] getDefaultExportHeaders() {
        String[] headers = {"主键", "创建时间", "更新时间", "备注", "版本号", "安卓版本号", "下载地址", "更新内容", "安卓版本号", "下载地址", "更新内容"};
        return headers;
    }

    @Override
    public String[] getDefaultExportColumns() {
        String[] columns = {"id", "createTime", "updateTime", "remark", "version", "androidVersion", "androidAddress", "androidInfo", "iosVersion", "iosAddress", "iosInfo"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/

    /**
     * 跳转到列表页面
     *
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/toConfig")
    public String toConfig(ModelMap modelMap, HttpServletRequest request,
                           HttpServletResponse response) {
        modelMap.put("m", versionService.getConfig());
        return getJspBasePath() + "/" + getBaseModuel() + "/" + getBaseModuel() + "Update";
    }

}
