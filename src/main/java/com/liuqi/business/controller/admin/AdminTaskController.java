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
@RequestMapping("/admin/task")
public class AdminTaskController extends BaseController {
    @Autowired
    private TaskService taskService;
    //加入Qulifier注解，通过名称注入bean


    @RequestMapping(value = "/toAdd")
    public String toAdd(ModelMap modelMap) {
        return "admin/task/taskAdd";
    }


    @RequestMapping(value = "/addjob")
    @ResponseBody
    public ReturnResponse addjob(@RequestParam(value = "jobClassName") String jobClassName,
                                 @RequestParam(value = "jobGroupName") String jobGroupName,
                                 @RequestParam(value = "cronExpression") String cronExpression,
                                 @RequestParam(value = "description") String description) throws Exception {
        TaskInfo info=new TaskInfo();
        info.setJobName(jobClassName);
        info.setCronExpression(cronExpression);
        info.setJobGroup(jobGroupName);
        info.setJobDescription(description);
        taskService.addJob(info);
        return ReturnResponse.backSuccess("添加成功","");
    }




    @RequestMapping(value = "/pausejob")
    @ResponseBody
    public ReturnResponse pausejob(@RequestParam(value = "jobClassName") String jobClassName, @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception {
        taskService.pause(jobClassName, jobGroupName);
        return ReturnResponse.backSuccess("暂停成功","");
    }



    @RequestMapping(value = "/resumejob")
    @ResponseBody
    public ReturnResponse resumejob(@RequestParam(value = "jobClassName") String jobClassName, @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception {
        taskService.resume(jobClassName, jobGroupName);
        return ReturnResponse.backSuccess("恢复成功","");
    }

    @RequestMapping(value = "/toUpdate")
    public String toUpdate(@RequestParam(value = "jobClassName") String jobClassName,
                           @RequestParam(value = "jobGroupName") String jobGroupName,
                           @RequestParam(value = "cronExpression") String cronExpression,
                           @RequestParam(value = "description") String description,ModelMap modelMap) {
        modelMap.put("jobClassName",jobClassName);
        modelMap.put("jobGroupName",jobGroupName);
        modelMap.put("cronExpression",cronExpression);
        modelMap.put("description",description);
        return "admin/task/taskUpdate";
    }
    @RequestMapping(value = "/updateJob")
    @ResponseBody
    public ReturnResponse rescheduleJob(@RequestParam(value = "jobClassName") String jobClassName,
                                        @RequestParam(value = "jobGroupName") String jobGroupName,
                                        @RequestParam(value = "cronExpression") String cronExpression,
                                        @RequestParam(value = "description") String description) throws Exception {
        TaskInfo info=new TaskInfo();
        info.setJobName(jobClassName);
        info.setCronExpression(cronExpression);
        info.setJobGroup(jobGroupName);
        info.setJobDescription(description);
        taskService.edit(info);
        return ReturnResponse.backSuccess("更新成功","");
    }


    @RequestMapping(value = "/deleteJob")
    @ResponseBody
    public ReturnResponse deletejob(@RequestParam(value = "jobClassName") String jobClassName, @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception {
        taskService.delete(jobClassName, jobGroupName);
        return ReturnResponse.backSuccess("删除成功","");
    }

    @RequestMapping(value = "/toList")
    public String toList(ModelMap modelMap) {
        return "admin/task/taskList";
    }

    @RequestMapping(value = "/queryList")
    @ResponseBody
    public DataResult queryList(ModelMap modelMap) {
        List<TaskInfo> infos = taskService.cronList();
        DataResult<TaskInfo> result=new DataResult<TaskInfo>();
        result.setCount(Long.valueOf(infos.size()+""));
        result.setData(infos);
        return result;
    }
    @RequestMapping(value = "/nowRun")
    @ResponseBody
    public ReturnResponse nowRun(@RequestParam(value = "jobClassName") String jobClassName, @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception {
        taskService.nowRun(jobClassName, jobGroupName);
        return ReturnResponse.backSuccess("执行成功","");
    }
}

