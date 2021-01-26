package com.liuqi.business.dto;

import com.liuqi.business.model.AlertsModelDto;
import lombok.Data;

import java.util.List;

@Data
public class AlertsDto {

    private String date;

    private List<AlertsModelDto> list;

    private Long compareDate;
}
