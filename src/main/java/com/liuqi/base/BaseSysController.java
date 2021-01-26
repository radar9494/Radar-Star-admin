package com.liuqi.base;


import com.liuqi.response.DataResult;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseSysController<T extends BaseModel, TDO extends T> extends BaseController {
    /**
     * 获取查询的servie 有实现类提供
     *
     * @return
     */
    protected abstract BaseService<T, TDO> getBaseService();


    /**
     * 获取基础模块 有实现类提供
     *
     * @return
     */
    protected abstract String getJspBasePath();

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
        return getJspBasePath() + "/list";
    }

    /***********list***************************************************************************************************************/
    /**
     * 列表页面预处理
     *
     * @param dto  请求参数
     * @param request
     */
    protected void listHandle(TDO dto, HttpServletRequest request) {
    }
    @RequestMapping("/queryList")
    @ResponseBody
    public DataResult getList(TDO tdo,HttpServletRequest request,
                              @RequestParam(defaultValue = "1", required = false) int page,
                              @RequestParam(defaultValue = "20", required = false) int limit) {
        this.listHandle(tdo, request);
        DataResult result = getData(tdo,page,limit);
        return result;
    }

    /**
     * 获取数据
     * @param dto
     * @param page
     * @param limit
     * @return
     */
    protected DataResult getData(TDO dto,int page,int limit){
        return this.getBaseService().queryPageByDto(dto, page, limit);
    }

}
