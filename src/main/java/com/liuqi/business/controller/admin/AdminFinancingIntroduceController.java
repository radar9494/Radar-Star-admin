package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.FinancingIntroduceModel;
import com.liuqi.business.model.FinancingIntroduceModelDto;
import com.liuqi.business.service.FinancingIntroduceService;
import com.liuqi.business.service.UploadFileService;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.InstanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/financingIntroduce")
public class AdminFinancingIntroduceController extends BaseAdminController<FinancingIntroduceModel,FinancingIntroduceModelDto> {

    @Autowired
    private FinancingIntroduceService financingIntroduceService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="financingIntroduce";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "add,update";
    @Override
    public BaseService getBaseService() {
    	return this.financingIntroduceService;
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
    private final static String DEFAULT_EXPORTNAME="融资融币介绍";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键", "创建时间", "更新时间", "备注", "版本号", "配置id", "标题", "内容"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id", "createTime", "updateTime", "remark", "version", "configId", "title", "content"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private UploadFileService uploadFileService;

    /**
     * 跳转到列表页面
     *
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getInfo")
    public String getInfo(@RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request,
                          HttpServletResponse response) {
        FinancingIntroduceModel inf = financingIntroduceService.getByConfigId(id);
        modelMap.put("m", inf);
        return getJspBasePath() + "/financingConfig/infoUpdate";
    }
    /**
     * （后台）修改
     * @param
     * @param request
     * @return
     */
    @RequestMapping("/updateP")
    @ResponseBody
    public ReturnResponse updateP(FinancingIntroduceModelDto dto, @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request) {
        FinancingIntroduceModel old = financingIntroduceService.getById(dto.getId());
        if (file != null) {
            old.setImage(uploadFileService.uploadImg(file,5));
        }
        InstanceUtil.getDiff_notInclude(old, dto, ",id,createTime,updateTime,version,image,");
        financingIntroduceService.update(old);
        return ReturnResponse.backSuccess();
    }

    @RequestMapping("/cleanCacheByIdP")
    @ResponseBody
    public ReturnResponse cleanCache(@RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        try {
            FinancingIntroduceModelDto m = financingIntroduceService.getByConfigId(id);
            financingIntroduceService.cleanCacheByModel(m);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }
    }
}
