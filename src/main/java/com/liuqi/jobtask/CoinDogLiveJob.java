package com.liuqi.jobtask;

import com.github.pagehelper.PageHelper;
import com.liuqi.business.enums.ShowEnum;
import com.liuqi.business.model.AlertsModelDto;
import com.liuqi.business.service.AlertsService;
import com.liuqi.third.coindog.CoinLiveList;
import com.liuqi.third.coindog.dto.CoinLivesDto;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * limit : （可选）获取的条数限制，取值（1-200），默认 20
 * id : （可选）从第几条快讯ID进行查询，默认 0
 * flag : （可选）结合id，是向上（大于）查询还是向下（小于）查询，取值up、down，默认down
 * 默认值 : id=0&flag=down，获取最新的 {limit} 条快讯记录
 * 使用 : id=100&flag=down, 获取小于id 100 的 {limit} 条快讯记录，适用于获取历史快讯
 * 使用 : id=200&flag=up, 获取大于id 200 的 {limit} 条快讯记录，适用于获取最新快讯（注意，
 * 依然会返回小于id为200的数据）
 * @author yjx
 */
@Slf4j
public class CoinDogLiveJob implements Job {

    @Autowired
    private CoinLiveList coinLiveList;

    @Autowired
    private AlertsService alertsService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        this.doIt();
    }

    //业务逻辑
    private void doIt(){

        int i;
        AlertsModelDto dto = new AlertsModelDto();
        StringBuilder sb;

        AlertsModelDto alerts = new AlertsModelDto();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");


        List<CoinLivesDto> lives = coinLiveList.getLiveList("10", "0", "down");

        if (lives==null){
            return;
        }
        //查询数据库最新获取的快讯 ID
        dto.setSortName("created_at");
        dto.setSortType("desc");
        PageHelper.startPage(1,1);
        List<AlertsModelDto> alertsModelDtos = alertsService.queryListByDto(dto, false);

        if (alertsModelDtos!=null && alertsModelDtos.size()>0){
            Long ct = alertsModelDtos.get(0).getCreatedAt();
            //过滤掉 小于 数据库已保存的最新数据
            lives = lives.stream().filter(p->Long.parseLong(p.getCreated_at())>ct).collect(Collectors.toList());
        }

        log.info("过滤后的快讯数量:"+lives.size());

        if (lives.size()>0){
            for (CoinLivesDto live : lives) {

                log.info("即将插入的快讯: ID:"+live.getId()+" 内容:"+live.getContent());

                sb = new StringBuilder(live.getContent());

                i = sb.indexOf("】");
                String content = sb.substring(i+1);
                String title = sb.substring(1,i);

                alerts.setStatus(ShowEnum.SHOW.getCode());
                alerts.setTitle(title);
                alerts.setContent(content);
                alerts.setAid(Long.valueOf(live.getId()));
                alerts.setGrade(Integer.valueOf(live.getGrade()));
                if (Integer.parseInt(live.getGrade())==5){
                    alerts.setHighlightColor("red");
                }else {
                    alerts.setHighlightColor(live.getHighlight_color());
                }
                alerts.setCreatedAt(Long.valueOf(live.getCreated_at()));
                alerts.setUpClickCount(Integer.valueOf(live.getUp_counts()));
                alerts.setDownClickCount(Integer.valueOf(live.getDown_counts()));
                alerts.setStaticDate(df.format(new Date()));

                alertsService.insert(alerts);
            }
        }
    }
}
