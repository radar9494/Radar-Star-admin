package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.OtcOrderRecordLogModel;
import com.liuqi.business.model.OtcOrderRecordLogModelDto;
import com.liuqi.business.service.OtcOrderRecordLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/admin/otcOrderRecordLog")
public class AdminOtcOrderRecordLogController extends BaseAdminController<OtcOrderRecordLogModel,OtcOrderRecordLogModelDto> {

    @Autowired
    private OtcOrderRecordLogService otcOrderRecordLogService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="otcOrderRecordLog";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add,update";
    @Override
    public BaseService getBaseService() {
    	return this.otcOrderRecordLogService;
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
    private final static String DEFAULT_EXPORTNAME="OTC日志记录";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","交易id","操作用户"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","recordId","name"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/


}
