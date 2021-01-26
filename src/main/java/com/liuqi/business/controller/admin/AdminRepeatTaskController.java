package com.liuqi.business.controller.admin;

import com.liuqi.base.BaseController;
import com.liuqi.business.dto.TaskInfo;
import com.liuqi.business.service.TaskService;
import com.liuqi.response.DataResult;
import com.liuqi.response.ReturnResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/admin/taskrepeat")
public class AdminRepeatTaskController extends BaseController {
    @Autowired
    private TaskService taskService;
    //加入Qulifier注解，通过名称注入bean

    @RequestMapping(value = "/pausejob")
    @ResponseBody
    public ReturnResponse pausejob(@RequestParam(value = "jobClassName") String jobClassName, @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception {
        taskService.pause(jobClassName, jobGroupName);
        return ReturnResponse.backSuccess("暂停成功", "");
    }


    @RequestMapping(value = "/resumejob")
    @ResponseBody
    public ReturnResponse resumejob(@RequestParam(value = "jobClassName") String jobClassName, @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception {
        taskService.resume(jobClassName, jobGroupName);
        return ReturnResponse.backSuccess("恢复成功", "");
    }


    @RequestMapping(value = "/reschedulejob")
    @ResponseBody
    public ReturnResponse rescheduleJob(@RequestParam(value = "jobClassName") String jobClassName,
                                        @RequestParam(value = "jobGroupName") String jobGroupName,
                                        @RequestParam(value = "cronExpression") String cronExpression) throws Exception {
        TaskInfo info = new TaskInfo();
        info.setJobName(jobClassName);
        info.setCronExpression(cronExpression);
        info.setJobGroup(jobGroupName);
        taskService.edit(info);
        return ReturnResponse.backSuccess("更新成功", "");
    }


    @RequestMapping(value = "/deletejob")
    @ResponseBody
    public ReturnResponse deletejob(@RequestParam(value = "jobClassName") String jobClassName, @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception {
        taskService.delete(jobClassName, jobGroupName);
        return ReturnResponse.backSuccess("删除成功", "");
    }


    @RequestMapping(value = "/toList")
    public String toList(ModelMap modelMap) {
        return "admin/task/repeatList";
    }

    @RequestMapping(value = "/queryList")
    @ResponseBody
    public DataResult queryList( ModelMap modelMap) {
        List<TaskInfo> infos = taskService.repeatList();
        DataResult<TaskInfo> result = new DataResult<TaskInfo>();
        result.setCount(Long.valueOf(infos.size() + ""));
        result.setData(infos);
        return result;
    }

}

