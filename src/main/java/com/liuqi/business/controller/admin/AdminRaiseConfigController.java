package com.liuqi.business.controller.admin;


import com.alibaba.fastjson.JSONObject;
import com.liuqi.anno.admin.CurAdminId;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.LoggerModelDto;
import com.liuqi.business.model.RaiseConfigModel;
import com.liuqi.business.model.RaiseConfigModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.RaiseConfigService;
import com.liuqi.business.service.UploadFileService;
import com.liuqi.response.ReturnResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import com.liuqi.business.dto.SelectDto;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/raiseConfig")
public class AdminRaiseConfigController extends BaseAdminController<RaiseConfigModel,RaiseConfigModelDto> {

    @Autowired
    private RaiseConfigService raiseConfigService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="raiseConfig";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.raiseConfigService;
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
    private final static String DEFAULT_EXPORTNAME="募集";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","币种","币种图片","rdb价格","usdt价格","目标人数","发行时间","发行总量","流通总量","白皮书","官网","区块查询","简介","显示状态","状态","开始建","结束时间"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","currencyId","image","rdbPrice","usdtPrice","targetNumber","publishTime","issuance","circulation","whitePaper","url","block","introduction","showStatus","status","startTime","endTime"};
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
    protected void toUpdateHandle(RaiseConfigModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(RaiseConfigModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Autowired
    private CurrencyService currencyService;

    private void getEnumList(ModelMap modelMap){
        List<CurrencyModelDto> all = currencyService.getAll();
        modelMap.put("currencyList",all);
    }



    @RequestMapping("/start")
    @ResponseBody
    public ReturnResponse start(Long id, @CurAdminId Long adminId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        return raiseConfigService.start(id);
    }




    @RequestMapping("/end")
    @ResponseBody
    public ReturnResponse end(Long id, @CurAdminId Long adminId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        return raiseConfigService.end(id);
    }


    @Autowired
    private UploadFileService uploadFileService;

    @RequestMapping("/addP")
    @ResponseBody
    public ReturnResponse addP(@CurAdminId Long adminId, @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request, RaiseConfigModelDto model) {
        if (file != null) {
            model.setImage(uploadFileService.uploadImg(file, 5));
        }
        //添加日志
        BaseAdminController.opeLogger.set(getLogger(adminId, LoggerModelDto.TYPE_ADD, "新增", JSONObject.toJSONString(model)));
        raiseConfigService.insert(model);
        return ReturnResponse.backSuccess();

    }



}
