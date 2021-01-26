package com.liuqi.business.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskInfo implements Serializable{

    /**任务名称*/
    private String jobName;

    /**任务分组*/
    private String jobGroup;

    /**任务描述*/
    private String jobDescription;

    /**任务状态*/
    private String jobStatus;

    /**任务表达式*/
    private String cronExpression;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 下次执行时间
     */
    private String nextTime;
    /**
     * 上次执行时间
     */
    private String preTime;
    private String jobStatusStr;



    //重复次数
    private int repeatCount;
    //已执行次数
    private int runCount;
    //间隔时间
    private Long interval;
    public String getJobStatusStr() {
        return jobStatusName(this.jobStatus);
    }

    public static String jobStatusName(String jobStatus) {
        String jobStatusStr="";
        if(jobStatus.equals("NORMAL")){
            jobStatusStr="正常";
        }else if(jobStatus.equals("RUNNING")){
            jobStatusStr="运行中";
        } else if(jobStatus.equals("PAUSED")){
            jobStatusStr="暂停";
        }else if(jobStatus.equals("ERROR")){
            jobStatusStr="异常";
        }
        return jobStatusStr;
    }
}
