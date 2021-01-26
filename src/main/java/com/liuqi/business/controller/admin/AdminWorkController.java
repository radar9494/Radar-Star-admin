package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.WorkDetailTypeEnum;
import com.liuqi.business.enums.WorkResultEnum;
import com.liuqi.business.enums.WorkStatusEnum;
import com.liuqi.business.model.WorkDetailModelDto;
import com.liuqi.business.model.WorkModel;
import com.liuqi.business.model.WorkModelDto;
import com.liuqi.business.model.WorkTypeModelDto;
import com.liuqi.business.service.*;
import com.liuqi.exception.NoLoginException;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/work")
public class AdminWorkController extends BaseAdminController<WorkModel,WorkModelDto> {

    @Autowired
    private WorkService workService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="work";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.workService;
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
    private final static String DEFAULT_EXPORTNAME="工单";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","工单内容","工单号","工单类型id","用户id","手机","邮箱","状态0未处理 1处理中 2完结"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","title","no","typeStr","userName","phone","email","statusStr"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private UserService userService;
    @Autowired
    private WorkTypeService workTypeService;
    @Autowired
    private WorkDetailService workDetailService;
    @Autowired
    private UploadFileService uploadFileService;
    @Override
    protected void listHandle(WorkModelDto dto, HttpServletRequest request) {
        //设置用户
        super.listHandle(dto, request);
        //设置用户
        String userName=dto.getUserName();
        if(StringUtils.isNotEmpty(userName)){
            Long userId=userService.queryIdByName(userName);
            dto.setUserId(userId);
        }
    }

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
    protected void toUpdateHandle(WorkModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(WorkModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);

        //查询
        List<WorkDetailModelDto> detailList=workDetailService.getByWork(t.getId());
        modelMap.put("detailList",detailList);
    }

    private void getEnumList(ModelMap modelMap){
        List<SelectDto> statusList= WorkStatusEnum.getList();
        modelMap.put("statusList",statusList);

        List<SelectDto> resultList= WorkResultEnum.getList();
        modelMap.put("resultList",resultList);

        List<WorkTypeModelDto> typeList= workTypeService.getUsing();
        modelMap.put("typeList",typeList);
    }


    /**
     * 回复
     *
     * @param workId
     * @param content
     * @param file1
     * @param file2
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @RequestMapping(value = "/reply")
    @ResponseBody
    public ReturnResponse reply(@RequestParam(value = "workId") Long workId,
                                @RequestParam(value = "content") String content,
                                @RequestParam(value = "file1", required = false) MultipartFile file1,
                                @RequestParam(value = "file2", required = false) MultipartFile file2,
                                @RequestParam(value = "file3", required = false) MultipartFile file3,
                                HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        WorkModel work = workService.getById(workId);
        if (work == null) {
            return ReturnResponse.backFail("未查询到工单");
        }
        if (WorkStatusEnum.END.getCode().equals(work.getStatus())) {
            return ReturnResponse.backFail("工单已完结");
        }
        String key = LockConstant.LOCK_WORK_ID + workId;
        RLock lock = null;
        String file1Path = "";
        String file2Path = "";
        String file3Path = "";
        try {
            if (file1 != null) {
                file1Path = uploadFileService.uploadImg(file1,5);
            }
            if (file2 != null) {
                file2Path = uploadFileService.uploadImg(file2,5);
            }
            if (file3 != null) {
                file3Path = uploadFileService.uploadImg(file3,5);
            }
            lock = RedissonLockUtil.lock(key);
            workService.reply(workId, content, file1Path, file2Path,file3Path, WorkDetailTypeEnum.SYS.getCode());
            return ReturnResponse.backSuccess();
        }finally {
            RedissonLockUtil.unlock(lock);
        }
    }
    /**
     * 完结
     *
     * @param workId
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @RequestMapping(value = "/end")
    @ResponseBody
    public ReturnResponse end(@RequestParam(value = "workId") Long workId, @RequestParam(value = "result",required = false,defaultValue = "0") Integer result,
                              HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        WorkModel work = workService.getById(workId);
        if (work == null) {
            return ReturnResponse.backFail("未查询到工单");
        }
        if (WorkStatusEnum.END.getCode().equals(work.getStatus())) {
            return ReturnResponse.backFail("工单已完结");
        }
        result= WorkResultEnum.DOING.getCode().equals(result)? WorkResultEnum.DOING.getCode(): WorkResultEnum.NOT.getCode();
        String key = LockConstant.LOCK_WORK_ID + workId;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            workService.sysEnd(workId,result);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }
}
