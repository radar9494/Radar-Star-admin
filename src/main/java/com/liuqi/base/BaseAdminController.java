package com.liuqi.base;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.anno.admin.CurAdminId;
import com.liuqi.business.model.LoggerModelDto;
import com.liuqi.response.DataResult;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.InstanceUtil;
import com.liuqi.utils.MyExcelUtil;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public abstract class BaseAdminController<T extends BaseModel, TDO extends T> extends BaseController {

    //操作日志
    public static ThreadLocal<LoggerModelDto> opeLogger = new ThreadLocal<LoggerModelDto>();

    //更新操作不包含字段
    public static String NOT_INCLUD_EFIELD=",id,createTime,updateTime,version,";
    /**
     * 获取查询的servie 有实现类提供
     *
     * @return
     */
    protected abstract BaseService<T, TDO> getBaseService();

    /**
     * 获取基础jsp返回路径 有实现类提供
     *
     * @return
     */
    protected abstract String getJspBasePath();

    /**
     * 获取基础模块 有实现类提供
     *
     * @return
     */
    protected abstract String getBaseModuel();

    protected abstract String getNotOperate();
    protected abstract String getDefaultExportName();
    protected abstract String[] getDefaultExportHeaders();
    protected abstract String[] getDefaultExportColumns();

    protected LoggerModelDto getLogger(Long adminId, Integer type, String title, String content) {
        LoggerModelDto log = new LoggerModelDto();
        log.setType(type);
        log.setTitle(title);
        log.setContent(content);
        log.setAdminId(adminId);
        //指定当前service实现类去添加操作日志信息
        log.setClassName(this.getBaseService().getClass().getName());
        return log;
    }
    /********toList******************************************************************************************************************/
    /**
     * 跳转到新增页面预处理
     *
     * @param modelMap
     * @param request
     * @param response
     */
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request,
                             HttpServletResponse response) {
    }

    /**
     * 跳转到列表页面
     *
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/toList")
    public String toList(ModelMap modelMap, HttpServletRequest request,
                         HttpServletResponse response) {
        this.toListHandle(modelMap, request, response);
        return getJspBasePath() + "/" + getBaseModuel() + "/"+getBaseModuel()+"List";
    }

    /***********list***************************************************************************************************************/
    /**
     * 列表页面预处理
     *
     * @param searchDto  请求参数
     * @param request
     */
    protected void listHandle(TDO searchDto, HttpServletRequest request) {
    }
    @RequestMapping("/queryList")
    @ResponseBody
    public DataResult getList(TDO tdo, HttpServletRequest request,
                              @RequestParam(defaultValue = "1", required = false) int page,
                              @RequestParam(defaultValue = "20", required = false) int limit) {
        this.listHandle(tdo, request);
        DataResult result = getData(tdo,page,limit);
        return result;
    }

    /**
     * 获取数据
     * @param searchDto
     * @param page
     * @param limit
     * @return
     */
    protected DataResult getData(TDO searchDto, int page, int limit){
        return this.getBaseService().queryPageByDto(searchDto, page, limit);
    }
    /*********Add*****************************************************************************************************************/
    /**
     * 跳转到新增页面预处理
     *
     * @param modelMap
     * @param request
     * @param response
     */
    protected void toAddHandle(ModelMap modelMap, HttpServletRequest request,
                            HttpServletResponse response) {
    }

    @RequestMapping("/toAdd")
    public String toAdd(ModelMap modelMap, HttpServletRequest request,
                        HttpServletResponse response) {
        this.toAddHandle(modelMap, request, response);
        return getJspBasePath() + "/" + getBaseModuel() + "/"+getBaseModuel()+"Add";
    }
    /**
     * 添加之前操作
     * @param modelMap
     * @param request
     * @param response
     */
    protected void addHandle(TDO t,Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    }
    @RequestMapping("/add")
    @ResponseBody
    public ReturnResponse add(@CurAdminId Long adminId, @Validated(AdminValid.class) TDO t, BindingResult bindingResult, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return ReturnResponse.backFail(this.getErrorInfo(bindingResult));
        }
        if(getNotOperate().contains("add")){
            return ReturnResponse.backFail("该方法不允许操作");
        }
        //预处理
        this.addHandle(t,adminId,modelMap,request,response);
        if (addLogger()) {
            opeLogger.set(getLogger(adminId, LoggerModelDto.TYPE_ADD, "新增", JSONObject.toJSONString(t)));
        }
        this.getBaseService().insert(t);
        return ReturnResponse.backSuccess();
    }

    /**
     * 是否新增日志
     *
     * @return
     */
    protected boolean addLogger() {
        return false;
    }
    /*********Update*****************************************************************************************************************/
    /**
     * 跳转到新增页面预处理
     *
     * @param modelMap
     * @param request
     * @param response
     */
    protected void toUpdateHandle(TDO t, ModelMap modelMap, HttpServletRequest request,
                                  HttpServletResponse response) {
    }

    @RequestMapping("/toUpdate")
    public String toUpdate(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, @RequestParam("id") Long id) {
        TDO m = this.getBaseService().getById(id);
        modelMap.put("m", m);
        this.toUpdateHandle(m,modelMap, request, response);
        return getJspBasePath() + "/" + getBaseModuel() + "/"+getBaseModuel()+"Update";
    }

    /**
     * 修改之前操作
     * @param t
     * @param modelMap
     * @param request
     * @param response
     */
    protected void updateHandle(TDO t,Long curUserId,ModelMap modelMap, HttpServletRequest request,
                               HttpServletResponse response) {
    }
    /**
     * 更新操作时  不拷贝的字段
     */
    protected String getNotIncludeField() {
        return NOT_INCLUD_EFIELD;
    }
    @RequestMapping("/update")
    @ResponseBody
    public ReturnResponse update(@CurAdminId Long adminId, @Validated(AdminValid.class) TDO t, BindingResult bindingResult, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return ReturnResponse.backFail(this.getErrorInfo(bindingResult));
        }
        if(getNotOperate().contains("update")){
            return ReturnResponse.backFail("该方法不允许操作");
        }
        TDO oldM = this.getBaseService().getById(t.getId());
        String oldMStr=JSONObject.toJSONString(oldM);
        InstanceUtil.getDiff_notInclude(oldM, t, getNotIncludeField());
        this.updateHandle(oldM,adminId,modelMap,request,response);
        if (updateLogger()) {
            opeLogger.set(getLogger(adminId, LoggerModelDto.TYPE_UPDATE, "更新", "修改前：" +oldMStr + "修改后：" +  JSONObject.toJSONString(t)));
        }
        this.getBaseService().update(oldM);
        return ReturnResponse.backSuccess();
    }

    /**
     * 是否新增日志
     *
     * @return
     */
    protected boolean updateLogger() {
        return false;
    }

    /*********View*****************************************************************************************************************/
    /**
     * 跳转到新增页面预处理
     *
     * @param m
     * @param modelMap
     * @param request
     * @param response
     */
    protected void toViewHandle(TDO m,ModelMap modelMap, HttpServletRequest request,
                             HttpServletResponse response) {
    }

    @RequestMapping("/toView")
    public String toView(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, @RequestParam("id") Long id) {
        TDO m = this.getBaseService().getById(id);
        modelMap.put("m", m);
        this.toViewHandle(m,modelMap, request, response);
        return getJspBasePath() + "/" + getBaseModuel() + "/"+getBaseModuel()+"View";
    }

    /*********导出*****************************************************************************************************************/
    /**
     * 导出
     * @param model
     * @param request
     * @param response
     * @return @
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(value = "/exportExc", method = RequestMethod.GET)
    public void exportExc(Model model,TDO tdo, HttpServletRequest request,
                          HttpServletResponse response) {
        this.listHandle(tdo, request);
        List dataset = getExportData(tdo);
        String[] headers = getDefaultExportHeaders();
        String[] columns = getDefaultExportColumns();
        MyExcelUtil.export(dataset, response, getDefaultExportName(), headers, columns);
    }
    @SuppressWarnings("rawtypes")
    protected List getExportData(TDO searchDto){
        return this.getBaseService().queryListByDto(searchDto,true);
    }

    /*********清除缓存*****************************************************************************************************************/
    @RequestMapping("/cleanCacheById")
    @ResponseBody
    public ReturnResponse cleanCache(@RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        TDO m = getBaseService().getById(id);
        getBaseService().cleanCacheByModel(m);
        return ReturnResponse.backSuccess();
    }

    @RequestMapping("/cleanAllCache")
    @ResponseBody
    public ReturnResponse cleanAllCache(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        getBaseService().cleanAllCache();
        return ReturnResponse.backSuccess();
    }
}
