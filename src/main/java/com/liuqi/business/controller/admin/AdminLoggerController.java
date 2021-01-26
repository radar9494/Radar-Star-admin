package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.LoggerModel;
import com.liuqi.business.model.LoggerModelDto;
import com.liuqi.business.service.LoggerService;
import com.liuqi.business.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/admin/logger")
public class AdminLoggerController extends BaseAdminController<LoggerModel, LoggerModelDto> {

    @Autowired
    private LoggerService loggerService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="logger";

    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add,update";
    @Override
    public BaseService getBaseService() {
    	return this.loggerService;
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
    //默认导出名称
    private final static String DEFAULT_EXPORTNAME="日志";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"编码","类型","标题","内容","操作管理员id"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","typeStr","title","content","adminId"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
}
