package com.liuqi.business.controller.admin;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.UserTreeDto;
import com.liuqi.business.model.UserLevelModel;
import com.liuqi.business.model.UserLevelModelDto;
import com.liuqi.business.model.UserModelDto;
import com.liuqi.business.service.UserLevelService;
import com.liuqi.business.service.UserService;
import com.liuqi.response.DataResult;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Struct;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/userLevel")
public class AdminUserLevelController extends BaseAdminController<UserLevelModel,UserLevelModelDto> {

    @Autowired
    private UserLevelService userLevelService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="userLevel";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add,update";
    @Override
    public BaseService getBaseService() {
        return this.userLevelService;
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
    private final static String DEFAULT_EXPORTNAME="用户层级";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","用户","领导","层级树","层级树信息"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","userName","parentName","treeLevel","treeInfo"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private UserService userService;
    @Override
    protected void listHandle(UserLevelModelDto dto, HttpServletRequest request) {
        //设置用户
        super.listHandle(dto, request);
        //设置用户
        String userName=dto.getUserName();
        if(StringUtils.isNotEmpty(userName)){
            Long userId=userService.queryIdByName(userName);
            dto.setUserId(userId);
        }
        //设置用户
        String parentName=dto.getParentName();
        if(StringUtils.isNotEmpty(parentName)){
            Long userId=userService.queryIdByName(parentName);
            dto.setParentId(userId);
        }

        String parentName2=dto.getParentName2();
        if(StringUtils.isNotEmpty(parentName2)){
            Long userId=userService.queryIdByName(parentName2);
            String searchTreeInfo= ",0,";
            if(userId>0) {
                UserLevelModelDto level = userLevelService.getByUserId(userId);
                searchTreeInfo=level.getTreeInfo();
            }
            dto.setSearchTreeInfo(searchTreeInfo);
        }
    }

    @RequestMapping("/toModifyParent")
    public String toUpdate(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, @RequestParam("id") Long id) {
        UserLevelModelDto m = userLevelService.getById(id);
        modelMap.put("m", m);
        this.toUpdateHandle(m, modelMap, request, response);
        return getJspBasePath() + "/" + getBaseModuel() + "/" + getBaseModuel() + "UpdateParent";
    }

    @RequestMapping("/updatePraent")
    @ResponseBody
    public ReturnResponse update(@RequestParam("id") Long id, @RequestParam("parentName") String parentName, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        try {
            UserLevelModelDto m = userLevelService.getById(id);
            if (m == null) {
                return ReturnResponse.backFail("数据异常");
            }
            Long parentId = 0L;
            if (StringUtils.isNotEmpty(parentName)) {
                UserModelDto parent = userService.queryByName(parentName);
                if (parent == null) {
                    return ReturnResponse.backFail("领导人不存在");
                }
                parentId = parent.getId();
            }
            userLevelService.changeLevel(m, parentId);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }
    }

    /**
     * 显示页面
     *
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/toShow")
    public String toShow(@RequestParam(value = "userId",defaultValue = "0") Long userId,
                         ModelMap modelMap, HttpServletRequest request,
                         HttpServletResponse response) {
        this.toListHandle(modelMap, request, response);
        modelMap.put("userId",userId);
        return getJspBasePath() + "/" + getBaseModuel() + "/" + getBaseModuel() + "Show";
    }


    @RequestMapping("/toTime")
    public String toTime(
                         ModelMap modelMap, HttpServletRequest request,
                         HttpServletResponse response) {
       // this.toListHandle(modelMap, request, response);
        return "admin/userTimeLevel/userLevelList";
    }



    @RequestMapping("/queryTimeList")
    @ResponseBody
    public DataResult queryTimeList(String parentName, HttpServletRequest request,
                              @RequestParam(defaultValue = "1", required = false) int page,
                              @RequestParam(defaultValue = "20", required = false) int limit) {
        if(StringUtils.isEmpty(parentName)){
            return new DataResult();
        }

        Long userId = userService.queryIdByName(parentName);

        List<Long> timestamSub = userService.getTimestamSub(userId);
        List<UserModelDto> byIds = userService.getByIds(timestamSub);
        DataResult result= new DataResult();
        result.setCount(Long.valueOf(timestamSub.size()));
        result.setData(byIds);
        return result;
    }



    /**
     * 显示页面
     *
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getChild")
    @ResponseBody
    public String getChild(@RequestParam(value = "pId", defaultValue = "0") Long pId, ModelMap modelMap, HttpServletRequest request,
                           HttpServletResponse response) {
        List<UserTreeDto> list = userLevelService.getTreeByParentId(pId);
        return JSONObject.toJSONString(list);
    }

    /**
     * 显示页面
     *
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getChildById")
    @ResponseBody
    public String getChildByName(@RequestParam(value = "userId",defaultValue = "0") Long userId, ModelMap modelMap, HttpServletRequest request,
                                 HttpServletResponse response) {
        if(userId>0){
            List<UserTreeDto> list=userLevelService.getTreeByUserId(userId);
            return JSONObject.toJSONString(list);
        }
        return "暂无数据";
    }

}
