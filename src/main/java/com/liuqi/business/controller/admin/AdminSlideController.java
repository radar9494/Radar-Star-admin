package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseConstant;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.SlideOutHrefEnum;
import com.liuqi.business.enums.SlideTypeEnum;
import com.liuqi.business.model.SlideModel;
import com.liuqi.business.model.SlideModelDto;
import com.liuqi.business.service.SlideService;
import com.liuqi.business.service.UploadFileService;
import com.liuqi.response.ReturnResponse;
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
@RequestMapping("/admin/slide")
public class AdminSlideController extends BaseAdminController<SlideModel, SlideModelDto> {

    @Autowired
    private SlideService slideService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="slide";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add,update";
    @Override
    public BaseService getBaseService() {
        return this.slideService;
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
    private final static String DEFAULT_EXPORTNAME="轮播图";
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
    private UploadFileService uploadFileService;
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
    protected void toUpdateHandle(SlideModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap) {
        modelMap.put("typeList", SlideTypeEnum.getList());
        modelMap.put("outHrefList", SlideOutHrefEnum.getList());
    }
    /**
     * （后台）添加
     * @param
     * @return
     */
    @RequestMapping("/addP")
    @ResponseBody
    public ReturnResponse addP(@RequestParam("file") MultipartFile file, SlideModelDto slideModel) {
        if(file.isEmpty()){
            return ReturnResponse.builder().code(ReturnResponse.RETURN_FAIL).msg("上传文件不能为空").build();
        }
        slideModel.setPath(uploadFileService.uploadImg(file,5));
        slideService.slideAdd(slideModel);
        return ReturnResponse.builder().code(ReturnResponse.RETURN_OK).msg("上传成功").build();

    }
    /**
     * （后台）修改
     * @param
     * @param request
     * @return
     */
    @RequestMapping("/updateP")
    @ResponseBody
    public ReturnResponse updateP(SlideModelDto slideModel, @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request) {
        SlideModel slide= slideService.getById(slideModel.getId());
        if(file!=null && file.getSize()>0){
            slide.setPath(uploadFileService.uploadImg(file,5));
        }
        slide.setRemark(slideModel.getRemark());
        slide.setHrefPath(slideModel.getHrefPath());
        slide.setPosition(slideModel.getPosition());
        slide.setOutHref(slideModel.getOutHref());
        slide.setStatus(slideModel.getStatus());
        slide.setType(slideModel.getType());
        slideService.update(slide);
        return ReturnResponse.backSuccess();
    }

    /**
     * (后台)删除
     * @param id
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("delete")
    public ReturnResponse slideDelete(@RequestParam Long id,HttpServletRequest request){
        Integer adminId = (Integer) request.getSession().getAttribute(BaseConstant.ADMIN_USERID_SESSION);
        boolean flag = slideService.slideDelete(id);
        if(flag){
            return ReturnResponse.backSuccess();
        }else{
            return ReturnResponse.backFail("操作失败");
        }
    }
}
