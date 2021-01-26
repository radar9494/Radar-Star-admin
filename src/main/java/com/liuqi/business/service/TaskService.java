package com.liuqi.business.service;


import com.liuqi.business.dto.TaskInfo;
import org.quartz.SchedulerException;

import java.util.List;
import java.util.Map;


public interface TaskService {

    /**
     * 定时任务
     *
     * @return
     */
    List<TaskInfo> cronList();

    /**
     * 重复任务
     *
     * @return
     */
    List<TaskInfo> repeatList();

    void addJob(TaskInfo info);

    void edit(TaskInfo info);

    void delete(String jobName, String jobGroup);

    void pause(String jobName, String jobGroup);

    void resume(String jobName, String jobGroup);

    boolean checkExists(String jobName, String jobGroup) throws SchedulerException;

    /**
     * 自定义定时任务  带参数
     *
     * @param jobName
     * @param jobGroup
     * @param corn
     * @param description
     * @param params
     */
    void addMyJob(String jobName, String jobGroup, String corn, String description, Map<String, Object> params);
    /**
     * 自定义定时任务  带参数
     *
     * @param jobName
     * @param jobGroup
     * @param corn
     * @param description
     * @param params
     */
    void editMyJob(String jobName, String jobGroup, String corn, String description, Map<String, Object> params);

    /**
     * 保存自定义重复定时任务 带参数
     *
     * @param jobName     任务名称   类.class.getName()
     * @param jobGroup    任务组名
     * @param description 描述
     * @param seconds     间隔秒数
     * @param repeatCount 重复次数
     * @param params      参数
     */
    void addRepeatJob(String jobName, String jobGroup, String description, int seconds, int repeatCount, Map<String, Object> params);

    /**
     * 添加延时任务  执行一次
     *
     * @param jobName
     * @param jobGroup
     * @param description
     * @param interval    执行时间
     * @param params
     */
    void addDelayedJob(String jobName, String jobGroup, String description, int interval, Map<String, Object> params);

    /**
     * 获取状态
     * @param jobName
     * @param jobGroup
     * @return
     */
    String getJobStatus(String jobName, String jobGroup);
    /**
     * 立即执行
     * @param jobName
     * @param jobGroup
     */
    void nowRun(String jobName, String jobGroup);
}