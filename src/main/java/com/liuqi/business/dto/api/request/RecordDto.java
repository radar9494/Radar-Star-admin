package com.liuqi.business.dto.api.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author tanyan
 * @create 2020-07=20
 * @description
 */
@Data
public class RecordDto implements Serializable {
    @NotNull(message = "币种不能为空")
    private String currency;

    private Integer type;

    private Long userId;

    private Date startTime;

    private Date endTime;

    private Integer pageSize=20;

    private Integer pageNum=1;
}
