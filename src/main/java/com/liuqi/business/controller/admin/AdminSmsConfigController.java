package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.SmsConfigModel;
import com.liuqi.business.model.SmsConfigModelDto;
import com.liuqi.business.service.SmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/smsConfig")
public class AdminSmsConfigController extends BaseAdminController<SmsConfigModel,SmsConfigModelDto> {

    @Autowired
    private SmsConfigService smsConfigService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="smsConfig";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add";
    @Override
    public BaseService getBaseService() {
    	return this.smsConfigService;
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
    private final static String DEFAULT_EXPORTNAME="短信配置";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","签名","国内key","国内私钥","国际key","开关0关 1开","每分钟条数","每小时条数","每天时条数"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","sign","key","secret","gjkey","onoff","minute","hour","day"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/

    @RequestMapping("/toConfig")
    public String toAdd(ModelMap modelMap, HttpServletRequest request,
                        HttpServletResponse response) {
        SmsConfigModelDto config=smsConfigService.getConfig();
        modelMap.put("m", config);
        modelMap.put("switchList", SwitchEnum.getList());
        return getJspBasePath() + "/" + getBaseModuel() + "/"+getBaseModuel()+"Update";
    }
}
