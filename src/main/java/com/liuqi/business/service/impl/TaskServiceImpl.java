package com.liuqi.business.service.impl;

import com.liuqi.business.dto.TaskInfo;
import com.liuqi.business.service.LoggerService;
import com.liuqi.business.service.TaskService;
import com.liuqi.exception.BusinessException;
import com.liuqi.utils.DateUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskServiceImpl implements TaskService {
    private Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired(required = false)
    private Scheduler scheduler;
    @Autowired
    private LoggerService loggerService;
    /**
     * 所有任务列表
     */
    public List<TaskInfo> cronList() {
        List<TaskInfo> list = new ArrayList<>();
        try {
            for (String groupJob : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.groupEquals(groupJob))) {
                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                    for (Trigger trigger : triggers) {
                        Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                        JobDetail jobDetail = scheduler.getJobDetail(jobKey);

                        String cronExpression = "", createTime = "", nextTime = "", preTime = "";

                        if (trigger instanceof CronTrigger) {
                            CronTrigger cronTrigger = (CronTrigger) trigger;
                            cronExpression = cronTrigger.getCronExpression();
                            createTime = cronTrigger.getDescription();
                            preTime = DateUtil.formatDate(cronTrigger.getPreviousFireTime(), "yyyy-MM-dd HH:mm:ss");
                            nextTime = DateUtil.formatDate(cronTrigger.getNextFireTime(), "yyyy-MM-dd HH:mm:ss");
                            TaskInfo info = new TaskInfo();
                            info.setJobName(jobKey.getName());
                            info.setJobGroup(jobKey.getGroup());
                            info.setJobDescription(jobDetail.getDescription());
                            info.setJobStatus(triggerState.name());
                            info.setCronExpression(cronExpression);
                            info.setCreateTime(createTime);
                            info.setNextTime(nextTime);
                            info.setPreTime(preTime);
                            list.add(info);
                        }
                    }
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }

        return list;
    }

    /**
     * 所有任务列表
     */
    public List<TaskInfo> repeatList() {
        List<TaskInfo> list = new ArrayList<>();
        try {
            for (String groupJob : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.groupEquals(groupJob))) {
                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                    for (Trigger trigger : triggers) {
                        Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                        JobDetail jobDetail = scheduler.getJobDetail(jobKey);

                        if (trigger instanceof SimpleTrigger) {
                            SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
                            TaskInfo info = new TaskInfo();
                            info.setJobName(jobKey.getName());
                            info.setJobGroup(jobKey.getGroup());
                            info.setJobDescription(jobDetail.getDescription());
                            info.setJobStatus(triggerState.name());
                            info.setRepeatCount(simpleTrigger.getRepeatCount());
                            info.setRunCount(simpleTrigger.getTimesTriggered());
                            info.setInterval(simpleTrigger.getRepeatInterval());
                            list.add(info);
                        }
                    }
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }

        return list;
    }

    /**
     * 保存定时任务
     *
     * @param info
     */
    @SuppressWarnings("unchecked")
    public void addJob(TaskInfo info) {
        String jobName = info.getJobName(),
                jobGroup = info.getJobGroup(),
                cronExpression = info.getCronExpression(),
                jobDescription = info.getJobDescription(),
                createTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        try {
            if (checkExists(jobName, jobGroup)) {
                logger.info("add job fail, job already exist, jobGroup:{}, jobName:{}", jobGroup, jobName);
            }

            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);

            CronScheduleBuilder schedBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withDescription(createTime).withSchedule(schedBuilder).build();


            Class<? extends Job> clazz = (Class<? extends Job>) Class.forName(jobName);
            JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(jobKey).withDescription(jobDescription).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException | ClassNotFoundException e) {
            logger.error("类名不存在或执行表达式错误,exception:{}", e.getMessage());
            throw new BusinessException("类名不存在或执行表达式错误,exception:{}");
        }
    }

    /**
     * 修改定时任务
     *
     * @param info
     */
    public void edit(TaskInfo info) {
        String jobName = info.getJobName(),
                jobGroup = info.getJobGroup(),
                cronExpression = info.getCronExpression(),
                jobDescription = info.getJobDescription(),
                createTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        try {
            if (!checkExists(jobName, jobGroup)) {
                logger.info("edit job fail, job is not exist, jobGroup:{}, jobName:{}", jobGroup, jobName);
            }
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
            JobKey jobKey = new JobKey(jobName, jobGroup);
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
            CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withDescription(createTime).withSchedule(cronScheduleBuilder).build();

            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            jobDetail.getJobBuilder().withDescription(jobDescription);
            HashSet<Trigger> triggerSet = new HashSet<>();
            triggerSet.add(cronTrigger);

            scheduler.scheduleJob(jobDetail, triggerSet, true);

        } catch (SchedulerException e) {
            logger.error("类名不存在或执行表达式错误,exception:{}", e.getMessage());
            throw new BusinessException("类名不存在或执行表达式错误,exception:{}");
        }
    }

    /**
     * 删除定时任务
     *
     * @param jobName
     * @param jobGroup
     */
    public void delete(String jobName, String jobGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        try {
            if (checkExists(jobName, jobGroup)) {
                scheduler.pauseTrigger(triggerKey);
                scheduler.unscheduleJob(triggerKey);
                logger.info("delete job, triggerKey:{},jobGroup:{}, jobName:{}", triggerKey, jobGroup, jobName);
            }
        } catch (SchedulerException e) {
            logger.error(e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 暂停定时任务
     *
     * @param jobName
     * @param jobGroup
     */
    public void pause(String jobName, String jobGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        try {
            if (checkExists(jobName, jobGroup)) {
                scheduler.pauseTrigger(triggerKey);
                logger.info("pause job success, triggerKey:{},jobGroup:{}, jobName:{}", triggerKey, jobGroup, jobName);
            }
        } catch (SchedulerException e) {
            logger.error(e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 重新开始任务
     *
     * @param jobName
     * @param jobGroup
     */
    public void resume(String jobName, String jobGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);

        try {
            if (checkExists(jobName, jobGroup)) {
                scheduler.resumeTrigger(triggerKey);
                logger.info("resume job success,triggerKey:{},jobGroup:{}, jobName:{}", triggerKey, jobGroup, jobName);
            }
        } catch (SchedulerException e) {
            logger.error(e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 验证是否存在
     *
     * @param jobName
     * @param jobGroup
     * @throws SchedulerException
     */
    public boolean checkExists(String jobName, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        return scheduler.checkExists(triggerKey);
    }

    /**
     * 自定义定时任务  带参数
     *
     * @param jobName
     * @param jobGroup
     * @param corn
     * @param description
     * @param params
     */
    @Override
    public void addMyJob(String jobName, String jobGroup, String corn, String description, Map<String, Object> params) {
        String createTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        try {
            if (checkExists(jobName, jobGroup)) {
                logger.info("add job fail, job already exist, jobGroup:{}, jobName:{}", jobGroup, jobName);
            }

            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);

            CronScheduleBuilder schedBuilder = CronScheduleBuilder.cronSchedule(corn).withMisfireHandlingInstructionDoNothing();
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withDescription(createTime).withSchedule(schedBuilder).build();


            Class<? extends Job> clazz = (Class<? extends Job>) Class.forName(jobName);
            JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(jobKey).withDescription(description).build();
            //设置参数
            jobDetail.getJobDataMap().putAll(params);
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException | ClassNotFoundException e) {
            logger.error("类名不存在或执行表达式错误,exception:{}", e.getMessage());
            throw new BusinessException("类名不存在或执行表达式错误,exception:{}");
        }
    }

    /**
     * 修改定时任务
     *
     */
    public void editMyJob(String jobName, String jobGroup, String cronExpression, String jobDescription, Map<String, Object> params) {
        String createTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        try {
            if (!checkExists(jobName, jobGroup)) {
                logger.info("edit job fail, job is not exist, jobGroup:{}, jobName:{}", jobGroup, jobName);
            }
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
            JobKey jobKey = new JobKey(jobName, jobGroup);
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
            CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withDescription(createTime).withSchedule(cronScheduleBuilder).build();

            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            jobDetail.getJobBuilder().withDescription(jobDescription);
            HashSet<Trigger> triggerSet = new HashSet<>();
            triggerSet.add(cronTrigger);
            //设置参数
            jobDetail.getJobDataMap().putAll(params);
            scheduler.scheduleJob(jobDetail, triggerSet, true);
        } catch (SchedulerException e) {
            logger.error("类名不存在或执行表达式错误,exception:{}", e.getMessage());
            throw new BusinessException("类名不存在或执行表达式错误,exception:{}");
        }
    }


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
    @SuppressWarnings("unchecked")
    @Override
    public void addRepeatJob(String jobName, String jobGroup, String description, int seconds, int repeatCount, Map<String, Object> params) {
        String createTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        try {
            //次数减一次  ，最后执行一次后删除任务
            repeatCount = repeatCount - 1;
            if (checkExists(jobName, jobGroup)) {
                logger.info("add job fail, job already exist, jobGroup:{}, jobName:{}", jobGroup, jobName);
            }
            //1创建JobDetial对象 , 并且设置选项
            Class<? extends Job> clazz = (Class<? extends Job>) Class.forName(jobName);
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
            JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(jobKey).withDescription(description).build();
            //2设置参数
            jobDetail.getJobDataMap().putAll(params);
            //2、通过 TriggerBuilder 创建Trigger对象
            SimpleScheduleBuilder schedBuilder = SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(seconds) //间隔时间
                    .withRepeatCount(repeatCount);//重复次数

            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withDescription(createTime) //创建时间
                    .withSchedule(schedBuilder).build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException | ClassNotFoundException e) {
            logger.error("类名不存在或执行表达式错误,exception:{}", e.getMessage());
            throw new BusinessException("类名不存在或执行表达式错误,exception:{}");
        }
    }


    /**
     * 添加延时任务  执行一次
     *
     * @param jobName
     * @param jobGroup
     * @param description
     * @param interval    延迟时间
     * @param params
     */
    @SuppressWarnings("unchecked")
    @Override
    public void addDelayedJob(String jobName, String jobGroup, String description, int interval, Map<String, Object> params) {
        String createTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        try {
            if (checkExists(jobName, jobGroup)) {
                logger.info("add job fail, job already exist, jobGroup:{}, jobName:{}", jobGroup, jobName);
            }
            //1创建JobDetial对象 , 并且设置选项
            Class<? extends Job> clazz = (Class<? extends Job>) Class.forName(jobName);
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
            JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(jobKey).withDescription(description).build();
            //2设置参数
            jobDetail.getJobDataMap().putAll(params);
            //2、通过 TriggerBuilder 创建Trigger对象

            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withDescription(createTime) //创建时间
                    .startAt(DateBuilder.futureDate(interval, DateBuilder.IntervalUnit.MINUTE))
                    .forJob(jobDetail)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException | ClassNotFoundException e) {
            logger.error("类名不存在或执行表达式错误,exception:{}", e.getMessage());
            throw new BusinessException("类名不存在或执行表达式错误,exception:{}");
        }
    }

    @Override
    public String getJobStatus(String jobName, String jobGroup) {
        String statusName="";
        try {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.groupEquals(jobGroup))) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    statusName=triggerState.name();
                    return statusName;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
        return statusName;
    }
    @Override
    public void nowRun(String jobName, String jobGroup) {
        Long time=System.currentTimeMillis();
        jobGroup="jobGroup"+time;
        try {
            String createTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            if (checkExists(jobName, jobGroup)) {
                logger.info("add job fail, job already exist, jobGroup:{}, jobName:{}", jobGroup, jobName);
            }
            //1创建JobDetial对象 , 并且设置选项
            Class<? extends Job> clazz = (Class<? extends Job>) Class.forName(jobName);
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
            JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(jobKey).withDescription("").build();
            //2、通过 TriggerBuilder 创建Trigger对象
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withDescription(createTime) //创建时间
                    .startNow()
                    .forJob(jobDetail)
                    .build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException | ClassNotFoundException e) {
            logger.error("类名不存在或执行表达式错误,exception:{}", e.getMessage());
            throw new BusinessException("类名不存在或执行表达式错误,exception:{}");
        }
    }
}