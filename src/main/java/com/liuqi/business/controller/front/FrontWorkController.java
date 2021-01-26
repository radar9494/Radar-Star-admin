package com.liuqi.business.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.enums.WorkDetailTypeEnum;
import com.liuqi.business.enums.WorkResultEnum;
import com.liuqi.business.enums.WorkStatusEnum;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.model.WorkModel;
import com.liuqi.business.model.WorkModelDto;
import com.liuqi.business.model.WorkTypeModelDto;
import com.liuqi.business.service.*;
import com.liuqi.exception.NoLoginException;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.response.ReturnResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 工单
 */
@Api(description = "工单")
@RequestMapping("/front/work")
@RestController
public class FrontWorkController extends BaseFrontController {
    @Autowired
    private WorkTypeService workTypeService;
    @Autowired
    private WorkService workService;
    @Autowired
    private WorkDetailService workDetailService;
    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private UserService userService;

    /**
     * 获取工单类型
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取工单类型")
    @PostMapping("/typeList")
    public ReturnResponse typeList(HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        List<WorkTypeModelDto> list = workTypeService.getUsing();
        return ReturnResponse.backSuccess(list);
    }

    /**
     * 发布工单
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "发布工单")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "typeId", value = "工单类型Id", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "string", name = "title", value = "标题", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "string", name = "content", value = "描述", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "string", name = "file1", value = "文件1", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "string", name = "file2", value = "文件2", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "string", name = "file3", value = "文件3", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "string", name = "phone", value = "联系人", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "string", name = "email", value = "邮箱", required = false, paramType = "query"),
    })
    @PostMapping("/publish")
    public ReturnResponse publish(@RequestParam(value = "typeId") Long typeId,
                                  @RequestParam(value = "title") String title,
                                  @RequestParam(value = "content") String content,
                                  @RequestParam(value = "phone", defaultValue = "") String phone,
                                  @RequestParam(value = "email", defaultValue = "") String email,
                                  @RequestParam(value = "file1", required = false) MultipartFile file1,
                                  @RequestParam(value = "file2", required = false) MultipartFile file2,
                                  @RequestParam(value = "file3", required = false) MultipartFile file3,
                                  HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = super.getUserId(request);
        String key = LockConstant.LOCK_WORK_USER + typeId + ":" + userId;
        RLock lock = null;
        String file1Path = "";
        String file2Path = "";
        String file3Path = "";
        try {
            // 第一个文件上传
            if (file1 != null) {
                file1Path = uploadFileService.uploadImg(file1, 5);
            }
            // 第一个文件上传
            if (file2 != null) {
                file2Path = uploadFileService.uploadImg(file2, 5);
            }
            // 第一个文件上传
            if (file3 != null) {
                file3Path = uploadFileService.uploadImg(file3, 5);
            }
            lock = RedissonLockUtil.lock(key);
            workService.publish(userId, typeId, title, phone, email, content, file1Path, file2Path, file3Path);
            return ReturnResponse.backSuccess();
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }


    /**
     * 回复工单
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "回复工单")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "workId", value = "工单Id", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "string", name = "content", value = "描述", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "string", name = "file1", value = "文件1", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "string", name = "file2", value = "文件2", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "string", name = "file3", value = "文件3", required = false, paramType = "query"),
    })
    @PostMapping("/reply")
    public ReturnResponse reply(@RequestParam(value = "workId") Long workId,
                                @RequestParam(value = "content") String content,
                                @RequestParam(value = "file1", required = false) MultipartFile file1,
                                @RequestParam(value = "file2", required = false) MultipartFile file2,
                                @RequestParam(value = "file3", required = false) MultipartFile file3,
                                HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = super.getUserId(request);
        WorkModel work = workService.getById(workId);
        if (work == null) {
            return ReturnResponse.backFail("未查询到工单");
        }
        if (!work.getUserId().equals(userId)) {
            return ReturnResponse.backFail("工单用户异常");
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
            // 第一个文件上传
            if (file1 != null) {
                file1Path = uploadFileService.uploadImg(file1, 5);
            }
            // 第一个文件上传
            if (file2 != null) {
                file2Path = uploadFileService.uploadImg(file2, 5);
            }
            // 第一个文件上传
            if (file3 != null) {
                file3Path = uploadFileService.uploadImg(file3, 5);
            }
            lock = RedissonLockUtil.lock(key);
            workService.reply(workId, content, file1Path, file2Path, file3Path, WorkDetailTypeEnum.USER.getCode());
            return ReturnResponse.backSuccess();
        }  finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    /**
     * 完结
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "完结")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "workId", value = "工单Id", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Long", name = "result", value = "解决0为解决 1已解决", required = true, paramType = "query"),
    })
    @PostMapping("/end")
    public ReturnResponse end(@RequestParam(value = "workId") Long workId,
                              @RequestParam(value = "result", defaultValue = "0") Integer result,
                              HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = super.getUserId(request);
        WorkModel work = workService.getById(workId);
        if (work == null) {
            return ReturnResponse.backFail("未查询到工单");
        }
        if (!work.getUserId().equals(userId)) {
            return ReturnResponse.backFail("工单用户异常");
        }
        if (WorkStatusEnum.END.getCode().equals(work.getStatus())) {
            return ReturnResponse.backFail("工单已完结");
        }
        result = WorkResultEnum.DOING.getCode().equals(result) ? WorkResultEnum.DOING.getCode() : WorkResultEnum.NOT.getCode();
        String key = LockConstant.LOCK_WORK_ID + workId;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            workService.userEnd(workId, result);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }


    /**
     * 获取我的工单
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取我的工单")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "status", value = "状态0处理中 1完结", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),
    })
    @PostMapping("/myWork")
    public ReturnResponse myWork(@RequestParam(defaultValue = "-1", required = false) final Integer status,
                                 @RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                 @RequestParam(defaultValue = "20", required = false) final Integer pageSize,
                                 HttpServletRequest request, ModelMap modelMap) throws NoLoginException {

        Long userId = super.getUserId(request);
        WorkModelDto search = new WorkModelDto();
        search.setUserId(userId);
        if (status >= 0) {
            search.setStatus(status);
        }
        PageInfo<WorkModelDto> pageInfo = workService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(pageInfo);
    }

    /**
     * 工单明细
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "工单明细")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "workId", value = "工单id", required = true, paramType = "query", defaultValue = "1")
    })
    @PostMapping("/myWorkDetail")
    public ReturnResponse myWorkDetail(@RequestParam(value = "workId", required = true) Long workId,
                                       HttpServletRequest request, ModelMap modelMap) throws NoLoginException {

        Long userId = super.getUserId(request);
        UserModel user = userService.getById(userId);
        WorkModel work = workService.getById(workId);
        if (work == null) {
            return ReturnResponse.backFail("未查询到工单");
        }
        if (!work.getUserId().equals(userId)) {
            return ReturnResponse.backFail("工单用户异常");
        }
        JSONObject obj = new JSONObject();
        obj.put("model", work);
        obj.put("detail", workDetailService.getByWork(work.getId()));
        return ReturnResponse.backSuccess(obj);
    }
}

